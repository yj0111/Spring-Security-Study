package com.study.springSecurity.domain.auth.util;

import com.study.springSecurity.domain.auth.dto.TokenDto;
import com.study.springSecurity.domain.auth.dto.UserDto;
import com.study.springSecurity.domain.user.entity.CredentialRole;
import com.study.springSecurity.global.exception.ExpiredTokenException;
import com.study.springSecurity.global.exception.UnAuthorizedAccessException;
import com.study.springSecurity.global.exception.WrongTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {

    private final Key key;
    private final String AUTHORITIES_KEY = "auth";
    @Value("${jwt.bearer.type}")
    private String BEARER_TYPE;
    @Value("${jwt.bearer.prefix}")
    private String BEARER_PREFIX;
    private final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60L * 30L;
    private final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60L * 60L * 24L * 7L; //7일

    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateTokenDto(String email) {
        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(email)
                .claim(AUTHORITIES_KEY, CredentialRole.USER)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(BEARER_PREFIX + accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String accessToken) {

        Claims claims = parseClaims(accessToken);

        String email = claims.getSubject();

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new UnAuthorizedAccessException();
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDto userDto = UserDto.builder()
                .email(email)
                .authorities(authorities)
                .build();

        return new UsernamePasswordAuthenticationToken(userDto, accessToken, authorities);
    }

    public boolean validateToken(String token) {

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new WrongTokenException();
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (UnsupportedJwtException e) {
            throw new WrongTokenException();
        } catch (IllegalArgumentException e) {
            log.error("잘못된 Argument를 입력했습니다.");
        }
        return false;
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}

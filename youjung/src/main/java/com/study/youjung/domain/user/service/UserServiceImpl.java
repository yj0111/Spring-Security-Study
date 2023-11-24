package com.study.youjung.domain.user.service;


import com.study.youjung.domain.auth.util.JwtProvider;
import com.study.youjung.domain.user.dto.response.UserResponseDto;
import com.study.youjung.domain.user.entity.User;
import com.study.youjung.domain.user.repository.CredentialRepository;
import com.study.youjung.domain.user.repository.UserRepository;
import com.study.youjung.global.exception.NotExistUserException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;

    @Override
    public UserResponseDto getUserById(Long userId) {
        log.info("UserServiceImpl_getUserById -> 사용자 정보 조회 시도");
        //사용자 정보 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(NotExistUserException::new);

        return user.toUserResponseDto();
    }


    @Override
    @Transactional
    public User getUserFromRequest(HttpServletRequest request) {
        log.info("UserServiceImpl_getUserFromRequest | Request의 토큰 값을 바탕으로 유저를 찾아옴");
        String accessToken = request.getHeader("Authorization").split(" ")[1];
        jwtProvider.parseClaims(accessToken);
        Claims accessTokenClaims = jwtProvider.parseClaims(accessToken);
        String userEmail = accessTokenClaims.getSubject();

        return userRepository.findByCredential(credentialRepository.findByEmail(userEmail).get()).get();
    }



}

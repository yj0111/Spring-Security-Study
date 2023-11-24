package com.study.youjung.domain.auth.controller;


import com.study.youjung.domain.auth.dto.request.LogoutReqDto;
import com.study.youjung.domain.auth.dto.request.UserDeleteReqDto;
import com.study.youjung.domain.auth.dto.response.LoginResponseDto;
import com.study.youjung.domain.auth.service.AuthService;
import com.study.youjung.domain.auth.service.KakaoAuthService;
import com.study.youjung.domain.auth.service.RedisService;
import com.study.youjung.domain.auth.util.HeaderUtil;
import com.study.youjung.domain.auth.util.JwtProvider;
import com.study.youjung.global.response.ResponseResult;
import com.study.youjung.global.response.SingleResponseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;
    private final AuthService authService;
    private final RedisService redisService;
    private final HeaderUtil headerUtil;
    private final JwtProvider jwtProvider;

    @GetMapping("/login/kakao")
    public ResponseEntity kakaoLogin(HttpServletRequest request) {
        log.info("AuthController_kakaoLogin -> 카카오 로그인");
        String code = request.getParameter("code");
        LoginResponseDto loginResponseDto = kakaoAuthService.kakaoLogin(code);
        return ResponseEntity.ok().headers(headerUtil.setTokenHeaders(loginResponseDto.getTokenDto()))
                .body(new SingleResponseResult<>(loginResponseDto.getUserResDto()));
    }

    @PatchMapping("/logout")
    public ResponseResult logout(@RequestBody LogoutReqDto logoutReqDto) {
        log.info("AuthController_logout -> 카카오 로그아웃");
        authService.logout(logoutReqDto);
        return ResponseResult.successResponse;
    }

    @DeleteMapping("/delete")
    public ResponseResult deleteUser(@RequestBody UserDeleteReqDto userDeleteReqDto) {
        log.info("AuthController_deleteUser -> 카카오 계정 탈퇴");
        authService.deleteUser(userDeleteReqDto);
        return ResponseResult.successResponse;
    }

    @PostMapping("/reissue")
    public ResponseResult reissue(HttpServletRequest request, HttpServletResponse response) {
        log.info("AuthController_reissue -> AccessToken 재발행");
        String newAccessToken = authService.reissueToken(request, response);
        return new SingleResponseResult<>(newAccessToken);
    }

    @GetMapping("/validateToken")
    public ResponseEntity validateToken(HttpServletRequest request) {
        log.info("Access Token 유효성 검사 중");
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring(7);
            if (jwtProvider.validateToken(accessToken)) {
                return ResponseEntity.ok(ResponseResult.successResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseResult.builder().messages("만료된 토큰입니다."));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseResult.builder().messages("만료된 토큰입니다."));
        }
    }
}
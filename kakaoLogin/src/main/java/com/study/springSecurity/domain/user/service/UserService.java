package com.study.springSecurity.domain.user.service;

import com.study.springSecurity.domain.user.dto.response.UserResponseDto;

public interface UserService {
    //내 정보 조회
    UserResponseDto getUserById(Long userId);
}

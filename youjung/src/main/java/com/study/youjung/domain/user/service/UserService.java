package com.study.youjung.domain.user.service;

import com.study.youjung.domain.user.dto.response.UserResponseDto;
import com.study.youjung.domain.user.entity.User;

import javax.servlet.http.HttpServletRequest;


public interface UserService {
    //내 정보 조회
    UserResponseDto getUserById(Long userId);

    User getUserFromRequest(HttpServletRequest request);
}

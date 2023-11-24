package com.study.springSecurity.domain.user.service.implement;

import com.study.springSecurity.domain.user.dto.response.UserResponseDto;
import com.study.springSecurity.domain.user.entity.User;
import com.study.springSecurity.domain.user.repository.UserRepository;
import com.study.springSecurity.domain.user.service.UserService;
import com.study.springSecurity.global.exception.NotExistUserException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDto getUserById(Long userId) {
        log.info("UserServiceImpl_getUserById -> 사용자 정보 조회 시도");
        //사용자 정보 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(NotExistUserException::new);

        return user.toUserResponseDto();
    }
}

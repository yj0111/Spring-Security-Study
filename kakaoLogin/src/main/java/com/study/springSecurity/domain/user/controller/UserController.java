package com.study.springSecurity.domain.user.controller;

import com.study.springSecurity.domain.user.dto.response.UserResponseDto;
import com.study.springSecurity.domain.user.service.UserService;
import com.study.springSecurity.global.response.ResponseResult;
import com.study.springSecurity.global.response.SingleResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

    @ApiOperation(value = "사용자 조회", notes = "사용자가 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "정보조회 성공"),
            @ApiResponse(code = 400, message = "정보조회 실패"),
    })
    @GetMapping("/{userId}")
    public ResponseResult getUser(@PathVariable Long userId) {
        log.info("UserController_getUser -> 사용자 정보 조회");
        UserResponseDto userResponseDto = userService.getUserById(userId);
        return new SingleResponseResult<>(userResponseDto);
    }
}

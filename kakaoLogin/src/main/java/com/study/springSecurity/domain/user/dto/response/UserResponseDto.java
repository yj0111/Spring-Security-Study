package com.study.springSecurity.domain.user.dto.response;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserResponseDto {
    private Long userId;
    private String userNickname;
    private String userImgUrl;
    private String credentialId;
    private String userEmail;
}
package com.study.youjung.domain.auth.dto.response;

import com.study.youjung.domain.auth.dto.TokenDto;
import com.study.youjung.domain.user.entity.CredentialRole;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserResDto {
    private Long userId;
    private String userNickname;
    private CredentialRole credentialRole;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private TokenDto tokenDto;
    private String credentialId;
}

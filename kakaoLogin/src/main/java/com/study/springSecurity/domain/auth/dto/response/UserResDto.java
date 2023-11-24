package com.study.springSecurity.domain.auth.dto.response;

import com.study.springSecurity.domain.auth.dto.TokenDto;
import com.study.springSecurity.domain.user.entity.CredentialRole;
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
    private String userImgUrl;
    private String userAddress;
    private boolean userBusiness;
    private boolean visited;
    private int userMoney;
    private String userPhone;
    private CredentialRole credentialRole;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private TokenDto tokenDto;
    private String credentialId;
    private String userEmail;
    private Long businessId;
}

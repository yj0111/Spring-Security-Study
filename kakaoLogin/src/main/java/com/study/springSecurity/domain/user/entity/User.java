package com.study.springSecurity.domain.user.entity;


import javax.persistence.*;

import com.study.springSecurity.domain.user.dto.response.UserResponseDto;
import com.study.springSecurity.global.entity.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String userNickname;

    @Column(nullable = false, length = 100)
    private String userImgUrl;

    @OneToOne
    @JoinColumn(name = "credential_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Credential credential;

    public UserResponseDto toUserResponseDto() {
        return UserResponseDto.builder()
            .userId(userId)
            .userNickname(userNickname)
            .userImgUrl(userImgUrl)
            .credentialId(credential.getCredentialId())
            .userEmail(credential.getEmail())
            .build();
    }
}

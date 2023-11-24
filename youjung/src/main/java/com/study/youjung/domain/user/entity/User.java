package com.study.youjung.domain.user.entity;

import com.study.youjung.domain.user.dto.response.UserResponseDto;
import com.study.youjung.global.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @OneToOne
    @JoinColumn(name = "credential_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Credential credential;

    public UserResponseDto toUserResponseDto() {
        return UserResponseDto.builder()
            .userId(userId)
            .userNickname(userNickname)
            .credentialId(credential.getCredentialId())
            .userEmail(credential.getEmail())
            .build();
    }
}

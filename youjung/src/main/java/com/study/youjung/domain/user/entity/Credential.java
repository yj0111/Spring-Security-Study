package com.study.youjung.domain.user.entity;

import com.study.youjung.global.entity.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Credential extends BaseTimeEntity {

	@Id
	@Column(length = 128)
	private String credentialId;

	@Column(length = 128)
	private String email;

	@Column(length = 10)
	private String credentialSocialPlatform;

	@NotNull
	@Enumerated(EnumType.STRING)
	private CredentialRole credentialRole;

	public String getRoleKey() {
		return this.credentialRole.getCode();
	}

}

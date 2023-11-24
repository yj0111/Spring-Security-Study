package com.study.springSecurity.domain.auth.dto.response;

import com.study.springSecurity.domain.auth.dto.TokenDto;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LoginResponseDto {
  private TokenDto tokenDto;
  private UserResDto userResDto;
}
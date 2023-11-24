package com.study.springSecurity.domain.auth.util;

import com.study.springSecurity.domain.auth.dto.TokenDto;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class HeaderUtil {
    public HttpHeaders setTokenHeaders(TokenDto tokenDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", tokenDto.getAccessToken());
        headers.add("refresh-token", tokenDto.getRefreshToken());
        return headers;
    }
}

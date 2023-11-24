package com.study.springSecurity.domain.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.study.springSecurity.domain.auth.dto.TokenDto;
import com.study.springSecurity.domain.auth.dto.kakao.KakaoAccountDto;
import com.study.springSecurity.domain.auth.dto.kakao.KakaoTokenDto;
import com.study.springSecurity.domain.auth.dto.response.LoginResponseDto;
import com.study.springSecurity.domain.auth.dto.response.UserResDto;
import com.study.springSecurity.domain.auth.util.JwtProvider;
import com.study.springSecurity.domain.user.entity.Credential;
import com.study.springSecurity.domain.user.entity.CredentialRole;
import com.study.springSecurity.domain.user.entity.User;
import com.study.springSecurity.domain.user.repository.CredentialRepository;
import com.study.springSecurity.domain.user.repository.UserRepository;
import com.study.springSecurity.global.exception.NotExistUserException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String KAKAO_CLIENT_ID;
	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	private String KAKAO_CLIENT_SECRET;
	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String KAKAO_REDIRECT_URI;
	@Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
	private String KAKAO_TOKEN_URI;
	@Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
	private String KAKAO_USER_INFO_URI;
	@Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
	private String KAKAO_GRANT_TYPE;
	private String KAKAO_DEFAULT_IMAGE = "https://showeat.s3.ap-northeast-2.amazonaws.com/profile.jpg";

	@Autowired
	private final RedisService redisService;
	private final JwtProvider jwtProvider;
	private final CredentialRepository credentialRepository;
	private final UserRepository userRepository;
	private TokenDto tokenDto;

	@Transactional
	public LoginResponseDto kakaoLogin(String code) {
		KakaoTokenDto kakaoTokenDto = getKakaoAccessToken(code);
		User user = getKakaoUserInfo(kakaoTokenDto);
		User existUser = userRepository.findByUserId(user.getUserId()).orElse(null);

		if (existUser == null) {
			log.info("존재하지 않는 회원정보입니다. 새로 저장합니다.");
			userRepository.save(user);
			log.info("member_id = {}", user.getUserId());
		}

		Long userId = user.getUserId();
		user = userRepository.findByUserId(userId).orElseThrow(NotExistUserException::new);
		Credential credential = credentialRepository.findByCredentialId(
			user.getCredential().getCredentialId()).orElseThrow(NotExistUserException::new);

		log.info("[login] 계정 확인 완료" + user.getUserNickname() + "로그인 성공!");
		log.info("grantType = {}", tokenDto.getGrantType());
		log.info("accessToken = {}", tokenDto.getAccessToken());
		log.info("refreshToken = {}", tokenDto.getRefreshToken());

		//refreshToken을 Redis에 저장
		redisService.setValues(credential.getEmail() + "_refreshToken", tokenDto.getRefreshToken());

		return LoginResponseDto.builder()
			.tokenDto(tokenDto)
			.userResDto(UserResDto.builder()
				.userId(user.getUserId())
				.userImgUrl(user.getUserImgUrl())
				.userNickname(user.getUserNickname())
				.credentialRole(user.getCredential().getCredentialRole())
				.createdDate(user.getCreatedDate())
				.modifiedDate(user.getModifiedDate())
				.tokenDto(tokenDto)
				.credentialId(credential.getCredentialId())
				.userEmail(credential.getEmail())
				.build())
			.build();
	}

	@Transactional
	public KakaoTokenDto getKakaoAccessToken(String code) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Accept", "application/json");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", KAKAO_GRANT_TYPE);
		params.add("client_id", KAKAO_CLIENT_ID);
		params.add("redirect_uri", KAKAO_REDIRECT_URI);
		params.add("code", code);
		params.add("client_secret", KAKAO_CLIENT_SECRET);

		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		log.info("token-uri = {}", KAKAO_TOKEN_URI);

		ResponseEntity<String> accessTokenResponse =
			restTemplate.postForEntity(KAKAO_TOKEN_URI, kakaoTokenRequest, String.class);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		KakaoTokenDto kakaoTokenDto = null;

		try {
			kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
		} catch (JsonProcessingException e) {
			log.error(e.toString());
		}

		return kakaoTokenDto;
	}

	@Transactional
	public User getKakaoUserInfo(KakaoTokenDto kakaoTokenDto) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + kakaoTokenDto.getAccess_token());

		HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);

		ResponseEntity<String> accountInfoResponse = restTemplate.exchange(
			KAKAO_USER_INFO_URI,
			HttpMethod.POST,
			accountInfoRequest,
			String.class
		);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		KakaoAccountDto kakaoAccountDto = null;

		try {
			kakaoAccountDto = objectMapper.readValue(accountInfoResponse.getBody(), KakaoAccountDto.class);
		} catch (JsonProcessingException e) {
			log.error(e.toString());
		}

		Map<String, Object> kakaoAccount = kakaoAccountDto.getKakao_account();
		Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");

		String email = (String)kakaoAccount.get("email");
		String nickname = (String)kakaoProfile.get("nickname");
		String picture = (String)kakaoProfile.get("profile_image_url");

		if (picture == null) {
			picture = KAKAO_DEFAULT_IMAGE;
		}

		tokenDto = jwtProvider.generateTokenDto(email);
		Credential credential = credentialRepository.findByEmail(email).orElse(null);

		if (credential != null) {
			log.info("이미 존재하는 email입니다. 바로 유저 정보를 반환합니다.");
			return userRepository.findByCredential(credential).orElse(null);
		}

		credential = Credential.builder()
			.email(email)
			.credentialId(UUID.randomUUID().toString())
			.credentialRole(CredentialRole.USER)
			.credentialSocialPlatform("kakao")
			.build();

		credentialRepository.save(credential);

		return User.builder()
			.credential(credential)
			.userNickname(nickname)
			.userImgUrl(picture)
			.build();
	}

}
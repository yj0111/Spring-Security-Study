# Spring-Security-Study
### ****1. OAuth, OAuth2.0 이란?****

**OAuth*(Open Authorization) :*** 사용자들이 비밀번호를 제공하지 않고, 다른 웹사이트 상의 자신들의 정보에 대해 웹사이트나 애플리케이션의 접근 권한을 부여하는 접근 위임을 위한 개방형 표준

사용자의 아이디와 비밀번호 없이 접근 권한을 위임받을 수 있다는 것은, 로그인 및 개인정보 관리 책임을 **'Third-Party Application*(google, kakao, naver 등)*'**에 위임할 수 있다는 것으로, 로그인 및 개인정보 관리에 대한 책임을 위임하는 것뿐만 아니라, 부여받은 접근 권한을 통해 Third-Party Application이 가지고 있는 사용자의 리소스 조회 가능 

**OAuth 2.0**은 다양한 클라이언트 환경에 적합한 인증*(Authentication)* 및 인가*(Authorization)*의 부여*(위임)* 방법*(Grant Type)*을 제공하고, 그 결과로 클라이언트에 접근 토큰*(Access Token)*을 발급하는 것에 대한 구조*(framework)*로 볼 수 있습니다.


### 2. 구성요소

1. **Client**
    
    Third-Party Application의 자원을 사용하기 위해 접근을 요청하는 Service 또는 Application을 뜻함  ⇒ 우리가 개발하려는 서비스
    
2. **Resource Owner**
    
    리소스의 소유자 또는 사용자를 뜻합니다. Third-Party Application의 보호된 자원에 접근할 수 있는 자격을 부여해 주는 주체이며, OAuth2 프로토콜 흐름에서는 클라이언트를 인증*(Authorize)*하는 역할을 수행합니다.
    
    인증이 완료되면 권한 획득 자격*(Authorization Grant)*을 Client에게 부여합니다.
    
    개념적으로는 Resource Owner가 자격을 부여하는 것이지만, 일반적으로 권한 서버*(Authorization Server)*가 Resource Owner와 Client 사이에서 중개 역할을 수행하게 됩니다.
    
3. **Authorization Server**
    
    권한을 가진 서버로 Resource Owner를 인증하며, Client의 접근 자격을 확인하고 Access Token을 발급해 주는 역할
    
4. **Resource Server**
    
    google, kakao, naver 등 사용자의 보호된 자원*(리소스)*을 가지고 있는 서버
    

공식 문서에 따르면 Authorization Server와 Resource Server는 별개로 구분되어 있지만, 구성에 따라 아키텍처가 달라질 수도 있음.

Authorization Server와 Resource Server가 동일한 서버일 수도 있고, 별개의 서버일 수도 있으며, 하나의 Authorization Server가 여러 개의 Resource Server에 액세스 토큰을 발급할 수도 있음.

### 3.동작과정( **Authorization Code Grant / 권한 부여 코드 승인 방식)**
![login](https://github.com/yj0111/Spring-Security-Study/assets/118320449/a96c16e8-6264-427c-8f24-70532ae00366)


1. 사용자 승인 후 임시 코드 발급: 사용자가 애플리케이션을 승인하면 인가서버는 임시 코드를 발급하고 이 코드를 Redirect URI로 전달합니다.
2. 임시 코드 교환: 애플리케이션은 해당 임시 코드를 인가서버로 다시 전송하여 액세스 토큰으로 교환합니다.
3. 클라이언트 인증: 애플리케이션이 액세스 토큰을 요청할 때 클라이언트 암호를 사용하여 인증합니다. 이것은 액세스 토큰의 보안을 강화하는 역할을 합니다.
4. 리프레시 토큰 활용: 액세스 토큰을 획득한 클라이언트 애플리케이션은 동시에 리프레시 토큰도 받습니다. 이 리프레시 토큰은 액세스 토큰이 만료된 경우에 사용됩니다.
5. Access Token의 안전한 전달: 이 방식은 액세스 토큰이 사용자 또는 브라우저에 직접 노출되지 않고, 애플리케이션으로 안전하게 전달되므로 보안성이 높습니다.
6. Authorization Code의 역할: Authorization Code는 중요한 데이터인 액세스 토큰을 브라우저를 통해 노출시키지 않기 위해 도입된 단계로, 클라이언트 애플리케이션과 백엔드 서버 간의 안전한 통신을 가능케 합니다.
7. 액세스 토큰 갱신: 액세스 토큰이 만료되면 클라이언트 애플리케이션은 리프레시 토큰을 사용하여 인가 서버에 새로운 액세스 토큰을 요청합니다.
8. 리프레시 토큰 검증: 인가 서버는 리프레시 토큰의 유효성을 확인하고, 유효한 경우 새로운 액세스 토큰을 발급합니다.
9. 새로운 액세스 토큰 획득: 클라이언트 애플리케이션은 이 새로운 액세스 토큰을 사용하여 API 요청을 다시 보낼 수 있습니다.


<hr>

# JWT Token

JWT(JSON Web Token)는 웹 표준으로써, 데이터의 JSON 객체를 사용하여 정보를 안전하게 전달할 수 있도록 설계된 토큰기반의 인증방식

JWT 는 URL , HTTP Header, HTML Form 과 같은 방식으로 전달할 수 있으며, 서버와 클라이언트 간의 인증 정보를 포함합니다

**JWT는 Header,Payload,Signature 세 부분으로 구성**

Header는 JWT의 타입과 암호화 알고리즘을 포함하며, JSON 형식으로 인코딩 됩니다.

Payload는 클레임 정보를 포함하며, JSON 형식으로 인코딩

**클레임 정보 : 사용자 ID, 권한 등의 원하는 정보를 포함**

**Signature는 Header와 Payload를 조합한 후 , 비밀 키를 사용하여 생성된 서명 값입니다**. 서명 값은 토큰의 무결성을 보장하며 , JWT 를 조작하지 않았다는 것을 검증 ( 해쉬 알고리즘 사용 )

### 인증방식

1. 클라이언트가 서버에 로그인 요청
2. 서버는 로그인 요청을 검증하고, 유효한 사용자라면 JWT 를 생성하여 클라이언트에게 반환
3. 클라이언트는 이후의 요청에 JWT 를 포함시켜서 전송
4. 서버는 JWT를 검증하여 , 클라이언트의 인증 여부를 판단

### 장점

1. 토큰 기반의 인증방식이므로, 서버 측에서 별도의 세션 저장소 유지 필요 X 
2. JSON 형식으로 인코딩 되므로, 다양한 플랫폼에서 쉽게 구현 가능
3. Signature를 사용하여 무결성을 보장하므로 토큰이 변조되었는지 쉽게 검증 가능

### 단점

1. JWT의 크기가 커질 경우, 네트워크 대역폭이 증가하게 됩니다
2. **한번 발급 한 후에는 내부 정보를 수정할 수 없으므로 , 만료시간을 짧게 설정**
3. JWT를 탈취당하면 해당 토큰을 사용한 모든 요청이 인증 되므로 보안 위협이 될 수 있음
    
    ⇒ HTTPS와 같은 보안 프로토콜을 사용하여 JWT 전송해야함


---
# Redis를 통한 Refresh Token 관리

# 1️⃣ Redis

- 일반 데이터베이스 같이 디스크(ssd)에 데이터를 쓰는 구조가 아니라 Remote(원격)에 위치
- NoSql DBMS(비관계형 데이터베이스)로 분류
- In-memory 기반의 데이터 관리 Server 시스템
    - In-memory
        - 메모리(RAM)에 저장하여 관리
        - 속도는 빠르지만 영속성을 보장하지 않고(데이터 유실 가능), 저장 공간이 한정되어있음
    - disk-base
        - 외부 저장 장치(디스크)
        - 데이터를 읽어 메모리에 올리고, 메모리에 올라간 데이터를 읽기 때문에 속도가 느림
- Key-value 의 비 관계형 구조로 Dictionary 형태로 존재

# 2️⃣ Redis 장점

- 적은 메모리로도 데이터를 저장할 수 있으며, 작성 속도가 빠름
- Key-Value 형태를 가지고 있기 때문에 키를 알고 있다면 조회 성능이 **O(1)**까지 나온다는 장점을 가짐
- 인메모리 DB 방식으로 **빠르게 접근이 가능**
- 캐시처럼 데이터 만료일을 정할 수 있음
- 휘발성인 In-Memory DB는 영구적으로 저장될 필요가 없는 Refresh token을 관리하기에 충분

# 3️⃣ Redis 단점

- 싱글쓰레드 기반으로 작동
- 한번에 딱 하나의 명령어만 실행하기 때문에 , 긴 처리시간이 필요한 명령어를 쓰면 불리하고 처리하기 전까지 다른 서비스 요청을 받아들일수 없고, 서버가 다운되는 현상
- 따라서 전체 데이터를 다루는 시간복잡도를 가진 O(N) 명령어 keys , flush , getall 는 주의해서 사용할 필요가 있다.

# 4️⃣ AccessToken in

- 서버에 **API 를 직접 요청(Access)** 할때 사용
- **사용자 인증정보를 담는다**
- **탈취의 위험과 혹여나 토큰 내용을 풀어버릴 수 있다는 위험 때문에 최소한의 사용자 정보만 담는 것을 권장**

# 5️⃣ RefreshToken

- 엑세스 토큰이 만료되었을때, 엑세스 토큰을 재발급(Refresh) 할 때 사용
- **Access Token의 재발급에 사용되어 사용자가 매번 로그인 과정을 거치지 않도록 한다.**

# 6️⃣Redis 저장 방식

- Refresh Token 은 단순 토큰이 아닌 사용자 정보를 담은 JWT 형태를 이용
- key : value = userPK : refreshToken 으로 저장
    - 이유
    - Header에서 RefeshToken을 해독해서 email 을 꺼내고, email을 key 로 Redis 를 조회
    - 조회 성공 ⇒ refresh token을 가져올수 있고, Redis 에서 조회한 refreshToken 과 클라이언트가 보낸 refresh Token 비교
    - 두 토큰 값이 매칭되면 정상 유저로 간주하고, AccessToken 재발급한다

# 7️⃣**토큰을 Redis에 저장하는 이유**

- 가볍다.
- Key-Value 구조를 지원하여, 조회 속도가 상당히 빠르다.

# 8️⃣**Access 토큰이 아닌 Refresh 토큰을 저장하는 이유**

- 리프레시 토큰은 만료 기간이 길다.
- 리프레시 토큰이 노출되었을 경우, AccessToken을 계속해서 갱신할 수 있으므로 너무 위험하다.

# 9️⃣**Redis**설정

```jsx
build.gradle

// Redis
	imple ingframework.boot:spring-boot-starter-data-redis'
```

```jsx
application.yml

#redis
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

# 🔟**Redis 에서 Refresh Token 관리 예시**

```jsx
RedisConfig.java

@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private String redisPort;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(redisHost);
		redisStandaloneConfiguration.setPort(Integer.parseInt(redisPort));
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
		return lettuceConnectionFactory;
	}

	// redis-cli 사용을 위한 설정
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		return redisTemplate;
	}
}
```

```jsx
RedisService.java

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;

	//조회
	public String getValues(String key) {
		ValueOperations<String, String> values = redisTemplate.opsForValue();
		return values.get(key);
	}
	
	//저장
	public void setValues(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
		redisTemplate.expire(key, 14, TimeUnit.DAYS);
	}
	
	//삭제
	public void delValues(String key){
		redisTemplate.delete(key+"_refreshToken");
	}
}
```

```jsx
// 저장 (KakaoAuthService.java)
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
					
		...(중간 생략)

        // 로그인 할때 refreshToken을 Redis에 저장
				redisService.setValues(credential.getEmail()+"_refreshToken", tokenDto.getRefreshToken());

	     ...(생략)
}

// 삭제 (AuthService.java)
@Transactional
    public void logout(LogoutReqDto logoutReqDto){
	       
				... (중간 생략)

        // 로그아웃 할때 redis 에서 refreshToken 삭제
        redisService.delValues(credential.getEmail());

        log.info("{} 님의 로그아웃 요청이 정상적으로 처리되었습니다.", user.getUserNickname());
    }

// 재발급 (AuthService.java)
@Transactional
    public void reissueToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("AuthService_reissueToken -> AccessToken 재발행");
				
	//1. Header에서 refreshToken 꺼내기
        String refreshToken = request.getHeader("refresh-token");

        //2. refreshToken JWT 해독
        Claims refreshTokenClaims = jwtProvider.parseClaims(refreshToken);
        
	//3. 해독 한 뒤 , email 찾기
	String email = refreshTokenClaims.getSubject();
        log.info("해독해서 뽑은 이메일 : " + email);

	****//4. redis에서 refreshToken 조회
        **String findRefreshToken = redisService.getValues(email + "_refreshToken");**

        if (findRefreshToken != null) {
            log.info(email + "님의 refreshToken : " + findRefreshToken);
						
	//5. 엑세스 토큰 JWT 재생성
            TokenDto newAccessToken = jwtProvider.generateTokenDto(findRefreshToken);
            
	//6. 재생성한 엑세스 토큰 Header에 저장
            response.setHeader("access-token", newAccessToken.getAccessToken());
            log.info(email + "님의 accessToken 재발급 : " + newAccessToken.getAccessToken());
        } else {
            // 유효하지 않거나 만료된 리프레시 토큰
            new InvalidRefreshTokenException();
        }
    }
```

----

kakao:
client-id: ebf2362b68dd4daf08301e8dd5a47ff5 # REST API키
client-secret: SIf3dJQvspMcSszhSfDig7uuXUNMmxGO # 카카오로그인 > 보안 > 코드 발급


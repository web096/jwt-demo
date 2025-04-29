# JWT 인증 및 자동 로그인 시스템 설계

## 📚 기술 스택
- Java 17
- Spring Boot 3.x
- Spring Security
- Redis
- jjwt (JWT 라이브러리)

---

## 🔥 인증 및 토큰 흐름 요약

| 토큰 종류 | 용도 | 수명 | 특징 |
|:---|:---|:---|:---|
| AccessToken | API 호출 인증용 | 짧음 (10~30분) | 만료되면 재발급 필요 |
| RefreshToken | 자동 로그인, AccessToken 재발급용 | 김 (30~90일) | 만료되면 자동 로그인 불가 |

---

## 🛡️ AccessToken & RefreshToken jti 관리

- AccessToken과 RefreshToken은 **서로 다른 jti(UUID)** 를 가진다.
- Redis에 각각 따로 관리한다.

```
jwt:refresh:{userIdx}:{refresh jti}
jwt:access:{userIdx}:{access jti}
```

---

## 🏛️ JWT 인증 흐름 (전체 프로세스)

```plaintext
[로그인 요청]
 ↓
AccessToken + RefreshToken 발급
 ↓
AccessToken 인증 기반 API 호출
 ↓
AccessToken 만료 시
 ↓
[RefreshToken으로 /v1/auth/reissue 호출]
 ↓
AccessToken + RefreshToken 재발급
 ↓
로그아웃 시 /v1/auth/logout
 ↓
Redis 세션 삭제
```

---

## 🔄 RefreshToken Rotate 전략

- 재발급 시마다 **새 RefreshToken 발급**
- Redis에 저장된 기존 RefreshToken 삭제 후 새로 저장
- 클라이언트는 새로운 RefreshToken로 갱신 저장해야 한다

✅ 탈취 위험 최소화  
✅ 세션 일관성 유지

---

## 🚪 자동 로그인(rememberMe) 동작 흐름

| 구분 | 동작 방식                                         |
|:---|:----------------------------------------------|
| rememberMe 체크 | RefreshToken 만료 기간을 길게 설정 (ex: 30일)            |
| rememberMe 미체크 | RefreshToken 만료 기간을 짧게 설정 (ex: 1일)            |
| 자동 로그인 흐름 | 앱 재시작 시 저장된 RefreshToken으로 AccessToken 자동 재발급 |

---

## 🛠️ 보안 강화 설계 포인트

- **JWT 서명 검증(SignatureVerification)**
- **Redis 기반 토큰 상태 검증 (jti matching)**
- **RefreshToken Rotate 적용**
- **AccessToken과 RefreshToken jti 분리 관리**
- **만료된 AccessToken에서도 Claims 추출 허용 (reissue를 위한)**
- **로그아웃 시 모든 세션 클린업**

---

# 📌 최종 요약

✅ AccessToken은 빠르게 만료, RefreshToken은 길게 유지  
✅ RefreshToken Rotate로 매번 새 토큰 발급  
✅ Redis에서 세션 상태 안전하게 관리  
✅ 자동 로그인은 RefreshToken 수명 동안만 유지  
✅ 필터(JwtAuthenticationFilter)와 서비스(AuthService)에서 역할 구분 검증

---
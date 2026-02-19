# BBang.Zip API 명세서

## 공통
- 외부 클라이언트 Base URL: `http://localhost:8080/api/v1` (Gateway 단일 진입)
- 내부 서비스 포트
  - Auth: 8081
  - Product: 8082
  - Order: 8083
  - Queue: 8084
- 인증이 필요한 API는 `X-User-Id` 헤더 사용 (MVP mock)

## 1. Auth / User API
### 1.1 아이디 중복 확인
`GET /auth/check-username?username={username}`

### 1.2 회원가입
`POST /auth/signup`

### 1.3 로그인
`POST /auth/login`

### 1.4 토큰 재발급
`POST /auth/refresh`

### 1.5 내 정보 조회
`GET /users/me`

### 1.6 내 정보 수정
`PATCH /users/me`

---
## 2. Product API
### 2.1 상품 목록 조회
`GET /products?page=0&size=10&keyword=소금&region=대전&minRating=4.5`

### 2.2 상품 상세 조회
`GET /products/{productId}`

### 2.3 상품 등록(관리자)
`POST /products`

### 2.4 상품 수정(관리자)
`PATCH /products/{productId}`

### 2.5 상품 삭제(관리자, soft delete)
`DELETE /products/{productId}`

---
## 3. Order API
### 3.1 주문 생성
`POST /orders`
- Header: `X-User-Id: user1`
- 내부 연동
  - Product Service 조회로 상품 검증
  - 수량 3개 이상 주문 시 Queue Service 자동 등록

### 3.2 주문 단건 조회
`GET /orders/{orderId}`

### 3.3 내 주문 목록
`GET /users/me/orders`

---
## 4. Payment API
### 4.1 결제 요청
`POST /payments`

### 4.2 결제 상태 조회
`GET /payments/{paymentId}`

---
## 5. Queue API
### 5.1 대기열 등록
`POST /queue/enroll`

### 5.2 대기열 순번 조회
`GET /queue/{queueId}?userId=user1`

### 5.3 대기열 이탈
`POST /queue/{queueId}/leave`

### 5.4 대기열 다음 사용자 승급(관리)
`POST /queue/{queueId}/promote`

### 5.5 대기열 스냅샷
`GET /queue/{queueId}/snapshot`

### 5.6 대기열 실시간 스트림(SSE)
`GET /queue/{queueId}/stream?userId=user1`

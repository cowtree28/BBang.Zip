# BBang.Zip API 명세서

## 공통
- Base URL
  - Auth/User: `http://localhost:8081/api/v1`
  - Product: `http://localhost:8082/api/v1`
  - Order/Payment: `http://localhost:8083/api/v1`
  - Queue: `http://localhost:8084/api/v1`
- 인증이 필요한 API는 `X-User-Id` 헤더를 사용(현재 MVP mock)
- 응답 포맷은 JSON

## 1. Auth / User API

### 1.1 아이디 중복 확인
`GET /auth/check-username?username={username}`

### 1.2 회원가입
`POST /auth/signup`
```json
{
  "username": "user1",
  "password": "pass1234",
  "name": "홍길동",
  "birthDate": "2000-01-01",
  "gender": "OTHER",
  "accountNumber": "111-222-333",
  "region": "대전",
  "consent": true
}
```

### 1.3 로그인
`POST /auth/login`
```json
{
  "username": "user1",
  "password": "pass1234"
}
```

### 1.4 토큰 재발급
`POST /auth/refresh`
```json
{
  "refreshToken": "refresh-..."
}
```

### 1.5 내 정보 조회
`GET /users/me`
- Header: `X-User-Id: user1`

### 1.6 내 정보 수정
`PATCH /users/me`
- Header: `X-User-Id: user1`
```json
{
  "name": "새 이름",
  "region": "세종"
}
```

---
## 2. Product API

### 2.1 상품 목록 조회
`GET /products?page=0&size=10&keyword=소금&region=대전&minRating=4.5`

### 2.2 상품 상세 조회
`GET /products/{productId}`

### 2.3 상품 등록(관리자)
`POST /products`
```json
{
  "name": "앙버터",
  "description": "버터와 팥앙금",
  "price": 4200,
  "stock": 50,
  "region": "대전 서구",
  "mainBread": false
}
```

### 2.4 상품 수정(관리자)
`PATCH /products/{productId}`

### 2.5 상품 삭제(관리자, soft delete)
`DELETE /products/{productId}`

---
## 3. Order API

### 3.1 주문 생성
`POST /orders`
- Header: `X-User-Id: user1`
```json
{
  "productId": 1,
  "productName": "소금빵",
  "quantity": 2,
  "priceSnapshot": 3500,
  "pickupLocation": "대전 중구"
}
```

### 3.2 주문 단건 조회
`GET /orders/{orderId}`

### 3.3 내 주문 목록
`GET /users/me/orders`
- Header: `X-User-Id: user1`

---
## 4. Payment API

### 4.1 결제 요청
`POST /payments`
```json
{
  "orderId": "ORD-1001",
  "cardNo": "1234-5678-1111-2222",
  "cardPassword": "12"
}
```

### 4.2 결제 상태 조회
`GET /payments/{paymentId}`

---
## 5. Queue API

### 5.1 대기열 등록
`POST /queue/enroll`
```json
{
  "queueId": "hotdeal-seongsimdang",
  "userId": "user1"
}
```

### 5.2 대기열 순번 조회
`GET /queue/{queueId}?userId=user1`

### 5.3 대기열 이탈
`POST /queue/{queueId}/leave`
```json
{
  "userId": "user1"
}
```

### 5.4 대기열 다음 사용자 승급(관리)
`POST /queue/{queueId}/promote`

### 5.5 대기열 스냅샷
`GET /queue/{queueId}/snapshot`

### 5.6 대기열 실시간 스트림(SSE)
`GET /queue/{queueId}/stream?userId=user1`
- Content-Type: `text/event-stream`
- 프론트에서 `EventSource`로 구독

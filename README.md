# BBang.Zip

빵집 픽업 주문 + 실시간 대기열 서비스를 위한 Spring MSA / React 프로젝트입니다.

## 아키텍처
- `gateway-service`(8080): 단일 진입점(API Gateway, 라우팅/CORS)
- `auth-service`(8081): 회원/인증/마이페이지
- `product-service`(8082): 빵 상품 CRUD/검색/필터
- `order-service`(8083): 주문/결제 + 서비스간 통신(상품 검증, 대기열 등록)
- `queue-service`(8084): 대기열 등록/조회/승급/SSE

## MSA 서비스 통신 포인트
- 클라이언트 → Gateway(8080)로만 요청
- Gateway → 각 서비스로 라우팅
- Order Service → Product Service: 주문 생성 시 상품 존재/정보 조회
- Order Service → Queue Service: 특정 조건(수량 3개 이상)에서 대기열 자동 등록

## 문서
- 전체 API 명세: `API.md`
- OpenAPI 초안: `docs/api-spec.yaml`

## 실행
### Backend
```bash
cd backend
gradle :services:gateway-service:bootRun
gradle :services:auth-service:bootRun
gradle :services:product-service:bootRun
gradle :services:order-service:bootRun
gradle :services:queue-service:bootRun
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

## 프론트 주요 경로
- `/` 서비스 개요
- `/auth` 회원가입/로그인
- `/products` 빵 목록/검색/주문
- `/queue` 대기열 등록/실시간 순번
- `/mypage` 내 정보
- `/roadmap` 개발 계획

# BBang.Zip

빵집 픽업 주문 + 실시간 대기열 서비스를 위한 Spring MSA / React 프로젝트입니다.

## 구성
- Backend(Spring Boot MSA)
  - `auth-service`(8081): 회원/인증/마이페이지
  - `product-service`(8082): 빵 상품 CRUD/검색/필터
  - `order-service`(8083): 주문/결제
  - `queue-service`(8084): 대기열 등록/조회/승급/SSE
- Frontend(React + Vite): 회원/상품/대기열/마이페이지 화면

## 문서
- 전체 API 명세: `API.md`
- OpenAPI 초안: `docs/api-spec.yaml`

## 실행
### Backend
```bash
cd backend
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

# BBang.Zip

대전/전국 빵집의 **픽업 주문 + 대기열 관리**를 위한 웹 서비스입니다.  
백엔드는 Spring 기반 MSA, 프론트는 React 기반으로 구성했습니다.

## 1) 아키텍처 개요

- `auth-service` : 회원가입/로그인/JWT 발급
- `product-service` : 빵/매장/재고/주빵 관리
- `order-service` : 주문 생성/조회/상태 전이
- `queue-service` : 인기 상품 대기열 등록/순번 처리/실시간 스트림
- (추후) API Gateway, Service Discovery, Config, Payment Worker

핵심 목표:
1. 트래픽 몰림 시 대기열로 주문 폭주 완화
2. 픽업 중심 주문 플로우 최적화
3. 관리자 상품 운영(가격/재고/핫딜/주빵) 지원

## 2) API 명세 (초안)

상세 스펙은 `docs/api-spec.yaml` 참고.

### 인증(Auth)
- `POST /api/v1/auth/signup` 회원가입
- `POST /api/v1/auth/login` 로그인 (access/refresh)
- `POST /api/v1/auth/refresh` 토큰 재발급
- `GET /api/v1/auth/check-username?username=` 아이디 중복확인

### 상품(Product)
- `GET /api/v1/products` 목록/검색/필터/페이지네이션
- `GET /api/v1/products/{productId}` 상세
- `POST /api/v1/products` 등록(관리자)
- `PATCH /api/v1/products/{productId}` 수정(관리자)
- `DELETE /api/v1/products/{productId}` 삭제(재고 0 + 주문 종료 후 실제 제거)

### 주문(Order)
- `POST /api/v1/orders` 주문 생성
- `GET /api/v1/orders/{orderId}` 주문 상세
- `GET /api/v1/users/me/orders` 내 주문 목록

### 결제(Payment)
- `POST /api/v1/payments` 결제 요청
- `GET /api/v1/payments/{paymentId}` 결제 상태

### 대기열(Queue)
- `POST /api/v1/queue/enroll` 대기열 등록
- `GET /api/v1/queue/{queueId}` 현재 순번 조회
- `GET /api/v1/queue/{queueId}/stream` SSE 실시간 순번 수신
- `POST /api/v1/queue/{queueId}/leave` 이탈 처리

### 마이페이지(User)
- `GET /api/v1/users/me` 내 정보 조회
- `PATCH /api/v1/users/me` 내 정보 수정

## 3) 파일 구조

```text
BBang.Zip/
├─ backend/
│  ├─ build.gradle
│  ├─ settings.gradle
│  └─ services/
│     ├─ auth-service/
│     ├─ product-service/
│     ├─ order-service/
│     └─ queue-service/
├─ frontend/
│  ├─ package.json
│  ├─ vite.config.ts
│  └─ src/
│     ├─ App.tsx
│     ├─ pages/
│     │  ├─ HomePage.tsx
│     │  ├─ QueuePage.tsx
│     │  └─ RoadmapPage.tsx
│     └─ styles.css
└─ docs/
   └─ api-spec.yaml
```

## 4) 이후 개발 계획

### Phase 1 - MVP
- 회원가입/로그인/JWT 인증
- 상품 CRUD + 페이지네이션 + 검색
- 주문 생성 + 결제 Mock
- 대기열 등록/조회/SSE 실시간 표시

### Phase 2 - 안정화
- Redis 기반 대기열(정렬/TTL/이탈 처리)
- Kafka 이벤트(`ORDER_CREATED`, `QUEUE_PROMOTED`)
- 결제 승인/취소 보상 트랜잭션 패턴

### Phase 3 - 운영
- Gateway + Rate limit + 서킷브레이커
- OpenTelemetry/Prometheus/Grafana 관측성
- CI/CD + Canary 배포

## 5) 실행 방법 (초기 스캐폴드)

### 백엔드
```bash
cd backend
./gradlew :services:auth-service:bootRun
./gradlew :services:product-service:bootRun
./gradlew :services:order-service:bootRun
./gradlew :services:queue-service:bootRun
```

### 프론트엔드
```bash
cd frontend
npm install
npm run dev
```

기본 화면:
- `/` 서비스 개요
- `/queue` 실시간 대기열 예시
- `/roadmap` 개발 로드맵

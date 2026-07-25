# FruitOpenMarket

멋쟁이사자처럼 백엔드 부트캠프 24기
4조 김다솜 / 김종범

**기간**: 2026.06.26 ~

---

## 프로젝트 방향

JWT 기반 인증을 적용한 쇼핑몰 프로젝트

### 주요 학습 목표

- Spring Security + JWT 인증 흐름 이해
- REST API 설계
- 장바구니 및 주문 로직 구현
- React 연동 경험
- 협업 및 Git 브랜치 전략 경험

### Tech Stack

- Java
- Spring Boot
- Spring Security
- JWT
- JPA
- MySQL
- Swagger
- React

---

## ERD 설계

| #   | Entity    | 필드                                                                             |
| --- | --------- | -------------------------------------------------------------------------------- |
| 1   | User      | id, username, name, email, password, role, createdAt                             |
| 2   | Product   | id, name, user_id(FK), price, stock, description, imageUrl, createdAt, updatedAt |
| 3   | Cart      | id, user_id(FK), createdAt                                                       |
| 4   | CartItem  | id, cart_id(FK), product_id(FK), quantity, createdAt, updatedAt                  |
| 5   | Order     | id, user_id(FK), totalPrice, status, createdAt                                   |
| 6   | OrderItem | id, order_id(FK), product_id(FK), quantity, price                                |

---

## 목표 단계

### 1차 목표

- [ ] 회원가입
- [ ] 로그인
- [ ] 상품 CRUD
- [ ] 장바구니
- [ ] Swagger 테스트

### 1-1차 목표 (React 연결) — **PASS**

- 로그인 페이지
- 상품 목록 페이지
- 장바구니 페이지

> 보류. 추후 Claude AI로 별도 제작 예정.

### 2차 목표

- [ ] 주문
- [ ] 결제
- [ ] 포인트
- [ ] 트랜잭션

세부 스펙은 7/13 회의에서 논의 (하단 참조).

---

## 진행 로그

| 날짜 | 내용                                       |
| ---- | ------------------------------------------ |
| 7/6  | 1차 결과물 confirm 예정 → 7/13로 일정 변경 |
| 7/13 | 1차 결과물 confirm                         |
| 7/13 | 강사님께 제출, 차후 피드백 예정            |

---

## 7월 13일 회의 내용

### Role

| Role  | 권한                                                  |
| ----- | ----------------------------------------------------- |
| USER  | 본인 상품 등록 / 수정 / 삭제 가능                     |
| ADMIN | 타 유저 상품 공개 상태 변경만 가능 (수정 / 등록 불가) |

### ProductStatus

- `ON_SALE` — 판매중
- `SOLD_OUT` — 품절

### OrderStatus (2차 목표)

| 기존 표현 | 변경 예정                       |
| --------- | ------------------------------- |
| 판매중    | `PENDING` (또는 `ON_SALE` 매칭) |
| 완료      | `PAID`                          |
| 취소      | `CANCELLED`                     |

---

## 일정

- **2차 목표 중간 피드백**: 다음 주 수요일 (7/22)
- **최종 목표 기한**: 7월 30일
- 코드리뷰/피드백은 가능하면 매주 수요일 진행 요청

---

## 2차 목표

### ERD 설계

| #   | Entity    | 필드                                                                |
| --- | --------- | ------------------------------------------------------------------- |
| 5   | Order     | id, user_id(FK), totalPrice, status, createdAt, updatedAt           |
| 6   | OrderItem | id, order_id(FK), product_id(FK), quantity, price                   |
| 7   | Payment   | id, order_id(FK, unique), amount, method, status, paidAt, createdAt |
| 8   | Point     | id, user_id(FK), amount, type, description, createdAt               |

---

### Payment 필드 설명

| 필드      | 설명                                                                                                |
| --------- | --------------------------------------------------------------------------------------------------- |
| order_id  | 이 결제가 어떤 주문에 대한 것인지 연결 (FK). unique인 이유: 주문 하나당 결제는 하나만 있어야 하므로 |
| amount    | 결제 금액                                                                                           |
| method    | 결제 수단 — CARD(카드), BANK_TRANSFER(계좌이체), POINT(포인트)                                      |
| status    | 결제 상태 (아래 표 참조)                                                                            |
| paidAt    | 결제가 실제로 완료된 시각                                                                           |
| createdAt | Payment 레코드가 생성된 시각 (결제 시도 시작 시점, paidAt과 다를 수 있음)                           |

---

### Point 필드 설명

| 필드        | 설명                                                         |
| ----------- | ------------------------------------------------------------ |
| amount      | 포인트 변동량 (양수: 적립, 음수: 사용)                       |
| type        | 변동 유형 — EARN(적립), USE(사용)                            |
| description | 변동 사유 (예: "주문#123 결제 적립", "주문#123 포인트 사용") |
| createdAt   | 포인트 변동 발생 시각                                        |

---

### Order.status (주문 상태)

| 값        | 의미                     |
| --------- | ------------------------ |
| PENDING   | 주문 생성됨, 결제 대기중 |
| PAID      | 결제 완료, 주문 확정     |
| CANCELLED | 주문 취소                |

### Payment.status (결제 상태)

| 값        | 의미           |
| --------- | -------------- |
| PENDING   | 결제 시도중    |
| COMPLETED | 결제 완료      |
| FAILED    | 결제 실패      |
| CANCELLED | 결제 취소/환불 |

### PaymentMethod / PaymentStatus / PointType (Enum 정의)

| Enum          | 값                                    |
| ------------- | ------------------------------------- |
| PaymentMethod | CARD, BANK_TRANSFER, POINT            |
| PaymentStatus | PENDING, COMPLETED, FAILED, CANCELLED |
| PointType     | EARN, USE                             |

### 논의 필요 사항

1. Order.status 자동 전환: PaymentService가 담당
   (PaymentService.completePayment() 안에서
   Payment.status와 Order.status를 같이 업데이트)

2. 포인트만 결제면 Payment 테이블이 사실상 Point랑 거의 중복이 됨
   Payment.amount, Point.amount가 하는 일이 비슷해짐
   연습용으로 쓰는 PG ( 토스페이먼츠, 카카오페이, 아임포트로 가능 하지만 시간이 많이 소요 될 수도 있음... 따라서 mock으로 하는 방향)

3. 처음에 포인트는 어떻게 생기나?
   - 회원가입 시 기본 지급? 이벤트 지급? 관리자가 부여?
   - 실제 "돈 주고 충전"하는 개념이 없으면 포인트 자체가 좀 붕 뜨는 느낌일 수 있음

# Integrated Queueing System

Java + Spring Boot 기반 인천공항 정보 제공 및 STOMP 채팅 프로젝트


## 응답 언어 지침

- 모든 결과값, 설명, 주석, 커밋 메시지, PR 설명 등은 반드시 한글로 작성한다.
- 코드 내 변수명, 함수명, 클래스명 등 식별자는 영문을 유지한다.


## 기술 스택

- 프레임워크: Spring Boot, Spring Security, Spring WebSocket (STOMP)
- ORM: JPA + QueryDSL
- DB: MySQL (메인)
- 메시징: Apache Kafka (이벤트 기반 비동기 처리)
- 검색엔진: Elasticsearch (항공편 검색, Nori 한글 형태소 분석기)
- 인증: OAuth2 (Google, Kakao) + JWT
- 메일: Spring Mail + Thymeleaf 템플릿
- 인프라: Docker Compose


## 아키텍처

- 상세 구조: @rules/architecture.md

### 주요 모듈

- `airport/` — 공항 정보 (항공편, 출국장, 주차장, 검색, 날씨, 이동시간)
- `messaging/` — STOMP 기반 실시간 채팅 (채팅방, 메시지)
- `member/` — 회원 관리 + 관심 항공편 구독
- `auth/` — OAuth2 소셜 로그인 + JWT 인증
- `config/` — 전역 설정 (Security, WebSocket, CORS, ES, QueryDSL)
- `common/` — 공통 유틸 (BaseTime, SnowflakeId, Cookie)
- `exception/` — 전역 예외 처리 (ErrorCode + @ControllerAdvice)


## 커밋 규칙

- 상세 구조 : @rules/commit.md

- 형식: <타입> <한글 설명>
- 타입: feat, fix, refactor, chore, docs, test
- 커밋 단위: 하나의 논리적 변경
- 브랜치: feat/기능명, fix/버그명, refactor/대상
- PR 대상: main


## 패턴

- 상세 규칙: @rules/pattern.md

- Response DTO: @Builder + 정적 팩토리 `from(Entity)`
- Request DTO: @Getter 기반, 필요 시 `toEntity()` 정의
- 엔티티 @Setter 금지, 비즈니스 메서드로 상태 변경
- 복잡 쿼리: QueryDSL (Custom Repository + Impl)
- 읽기 전용: @ReadOnlyTransaction


## 에러 핸들링

- 상세 규칙: @rules/errorHandler.md

- ChatException(HttpStatus, ErrorCode)으로 예외 발생
- @ControllerAdvice에서 ErrorDto로 일괄 변환
- Service 레이어가 예외 처리의 주 책임


## 문서 동기화 규칙

- 코드 변경 작업이 완료된 후, 아래 문서에 영향이 있는지 반드시 확인하고 사용자에게 업데이트 여부를 질문한다.

  - `CLAUDE.md` — 기술 스택, 모듈 구조, 패턴 등 개요 수준 변경
  - `rules/architecture.md` — 디렉터리 구조, 로직 흐름
  - `rules/pattern.md` — 코딩 패턴, DTO/엔티티/Repository 규칙 등의 변경
  - `rules/errorHandler.md` — 에러 핸들링 방식 변경
  - `rules/commit.md` — 커밋 규칙 변경
  
- 질문 형식: "이번 변경으로 인해 [문서명]에 반영할 내용이 있습니다. 문서를 업데이트할까요?"
- 문서에 영향이 없는 단순 버그 수정이나 기존 패턴을 따르는 변경은 질문을 생략할 수 있다.
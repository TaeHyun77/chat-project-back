# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 언어 규칙

모든 결과값, 설명, 주석, 커밋 메시지, PR 설명 등은 반드시 **한글**로 작성한다.

## Project Overview

인천공항 정보 제공 및 실시간 채팅 서비스. Spring Boot backend + React frontend.

- Provides real-time departure lounge congestion data, flight information, and WebSocket-based chat
- External data fetched from 공공데이터포털 API and synced via scheduled tasks

## Build & Run Commands

```bash
./gradlew build          # Build (generates QueryDSL Q-classes in src/main/generated/)
./gradlew bootRun        # Run application
./gradlew test           # Run all tests
./gradlew test --tests "com.example.chat.PlaneServiceTest"  # Run single test class
docker-compose up        # Run full stack (MySQL, Redis, App, Frontend)
```

Requires a `.env` file in project root with DB credentials, JWT secret, and API keys (see `application.properties` for variable names).

## Architecture

### Package Structure

```
com.example.chat/
├── airport/
│   ├── ApiService.java           # External API calls (kept outside transactions)
│   ├── AirportScheduler.java     # Scheduled sync: departure every 3min, flights every 1min
│   ├── plane/                    # Flight data (Plane entity, CRUD, QueryDSL)
│   ├── Departure/                # Terminal congestion data
│   └── aspect/PlaneDataAspect.java  # AOP for plane data handling
├── chat/
│   ├── chat/                     # Chat messages (Chat entity, WebSocket/REST)
│   └── chatRoom/                 # Chat rooms (UUID-based, session tracking)
├── member/                       # Users, roles, auth
├── jwt/                          # JwtUtil (token creation/validation), JwtFilter
├── oauth/                        # Google OAuth 2.0 (CustomOAuth2UserService, success handler)
├── refresh_retoken/              # Refresh token entity and reissuance
├── config/                       # SecurityConfig, WebsocketConfig, QuerydslConfig, WebCorsConfig
├── exception/                    # ChatException + ErrorCode enum
└── ReadOnlyTransaction.java      # Custom annotation: @Transactional(readOnly=true, propagation=SUPPORTS)
```

### Key Patterns

**Repository composition:** Every domain uses `FooRepository extends JpaRepository` + `FooRepositoryCustom` interface + `FooRepositoryImpl` (QueryDSL). QueryDSL Q-classes are generated at build time.

**DTO factory methods:** `PlaneResDto.from(Plane entity)` static factory pattern used throughout.

**Transaction discipline:** `ApiService` is kept outside `@Transactional` boundaries—external API calls happen before/after DB transactions to minimize lock time. Read-only queries use `@ReadOnlyTransaction`.

**Security:** Stateless JWT auth. Access token: 30min, Refresh token: 3 days. `JwtFilter` validates on every request. OAuth2 (Google) login issues JWT on success via `CustomSuccessHandler`.

**WebSocket:** STOMP over SockJS at `/ws`. App destination prefix `/app`, topic prefix `/topic`. `SessionEventListener` tracks connected user count.

**Scheduling & data lifecycle:**
- Departure data: 2-day retention, synced every 3 minutes
- Flight data: 4-day window (yesterday → day after tomorrow), synced every 1 minute, midnight cleanup via cron
- Codeshare filtering: only Master flights stored

**Entity auditing:** `BaseTime` abstract class with `@CreatedDate`/`@LastModifiedDate` (format: `"yyyy-MM-dd kk:mm:ss"`). `@EnableJpaAuditing` on main class.

**Error handling:** Throw `ChatException(ErrorCode.XXX)` — centralized error codes in `ErrorCode` enum.

## Tech Stack

- Java 17, Spring Boot 3.4.3
- MySQL + Spring Data JPA (Hibernate, auto-update DDL)
- Redis for caching
- QueryDSL 5.0.0 (Jakarta)
- JWT via JJWT 0.12.3
- WebSocket + STOMP + SockJS
- Lombok (`@Slf4j`, `@Builder`, etc.)
- Docker (Alpine-based, resource-limited: 0.4 CPU / 400MB RAM)

## 문서 동기화 규칙

- 코드 변경 작업이 완료된 후, 아래 문서에 영향이 있는지 반드시 확인하고 사용자에게 업데이트 여부를 질문한다.

  - `CLAUDE.md` — 기술 스택, 모듈 구조, 패턴 등 개요 수준 변경
  - `rules/architecture.md` — 디렉터리 구조, 로직 흐름
  - `rules/pattern.md` — 코딩 패턴, DTO/엔티티/Repository 규칙 등의 변경
  - `rules/errorHandler.md` — 에러 핸들링 방식 변경
  - `rules/commit.md` — 커밋 규칙 변경

- 질문 형식: "이번 변경으로 인해 [문서명]에 반영할 내용이 있습니다. 문서를 업데이트할까요?"
- 문서에 영향이 없는 단순 버그 수정이나 기존 패턴을 따르는 변경은 질문을 생략할 수 있다.

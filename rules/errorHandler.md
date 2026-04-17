# 에러 핸들링 규칙

## 구조

- `ChatException` (RuntimeException) — 프로젝트 유일한 커스텀 예외
- `ErrorCode` enum — 에러 코드 + 메시지 정의
- `CustomExceptionHandler` (@ControllerAdvice) — 전역 예외 처리
- `ErrorDto` — 에러 응답 형식 (code, msg, detail)

## 예외 처리 패턴

- 데이터 조회 실패 — orElseThrow
- 비즈니스 로직 검증 — 조건문 + throw
- 체크 예외 변환 — try-catch
- 복잡한 검증 — private 메서드 분리

## 계층별 역할
- Controller: 인증 null 체크 정도만 수행, 나머지는 서비스에 위임
- Service: 비즈니스 예외 발생의 주 책임
- @ControllerAdvice: ChatException → ErrorDto 변환 및 HTTP 응답

## HttpStatus 사용 기준
- <상황> <HttpStatus>

- 데이터 없음 : NOT_FOUND (404)
- 잘못된 요청/입력 : BAD_REQUEST (400)
- 인증 실패 : UNAUTHORIZED (401)
- 권한 없음 : FORBIDDEN (403)
- 중복 데이터 : CONFLICT (409)

## ErrorCode 추가 규칙
- 카테고리별 그룹핑 (NOT_FOUND, API, ...)
- 네이밍: 대문자 + 스네이크 케이스
- 메시지: 한글로 작성

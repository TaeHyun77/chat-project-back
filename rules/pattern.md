# 코딩 패턴 규칙

## DTO 패턴

### Response DTO
- 엔티티 반환 시 반드시 DTO 형식으로 한다.
- @Builder + @Getter + @AllArgsConstructor
- 정적 팩토리 메서드 `from(Entity)` 로 엔티티 → DTO 변환

### Request DTO
- 엔티티 변환이 필요하면 toEntity() 메서드 정의

## 엔티티 패턴
- @Setter 사용 금지 
- 기본 상속: BaseTime (createdAt, modifiedAt 자동 관리)
- 배치 처리 엔티티: BaseBatch 상속 (Snowflake ID 자동 생성)
- 연관관계: @ManyToOne(fetch = LAZY) 기본, 양방향 시 cascade + orphanRemoval 설정
- @Builder는 커스텀 생성자에 적용 (ID, 컬렉션 필드 제외)

## Repository 패턴
- 단순 쿼리: Spring Data JPA 메서드 네이밍
- 복잡 쿼리: {Entity}RepositoryCustom 인터페이스 + {Entity}RepositoryImpl (QueryDSL)
- N+1 방지: QueryDSL에서 .fetchJoin() 사용

## Controller 패턴
- 성공 응답: 객체 직접 반환 (ResponseEntity 미사용)
- 삭제/수정 등 본문 없는 응답: ResponseEntity.noContent().build()
- 예외 처리: 서비스에 위임 (컨트롤러에서 try-catch 하지 않음)
- 페이징: Slice 사용
- WebSocket: @MessageMapping (STOMP)

## 트랜잭션
- 쓰기: @Transactional
- 읽기 전용: @ReadOnlyTransaction (커스텀 어노테이션, readOnly + SUPPORTS)

## 네이밍
- 패키지: 도메인 중심 (airport/plane/, messaging/chat/)
- DTO: {Entity}ResDto, {Entity}ReqDto
- 팩토리 메서드: from() (Entity → DTO), toEntity() (DTO → Entity)

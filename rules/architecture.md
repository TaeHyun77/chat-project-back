## 디렉터리 구조

```
chat-project-back/
├── docker-compose.yml                  # 인프라 컨테이너 구성
├── Dockerfile                          # 애플리케이션 빌드 이미지
├── build.gradle                        # Gradle 빌드 설정
├── rules/                              # 프로젝트 규칙 문서
│   ├── architecture.md                 # 아키텍처 문서 (현재 파일)
│   ├── commit.md                       # 커밋 규칙
│   ├── errorHandler.md                 # 에러 핸들링 규칙
│   ├── pattern.md                      # 코딩 패턴 규칙
│   └── test.md                         # 테스트 규칙
├── docker/
│   └── elasticsearch/                  # ES 커스텀 설정/플러그인
│
└── src/
    ├── main/
    │   ├── java/com/example/chat/
    │   │   │
    │   │   ├── ChatApplication.java            
    │   │   │
    │   │   ├── airport/                        # [도메인] 공항 정보 모듈
    │   │   │   ├── AirportScheduler.java       #   스케줄러 - 데이터 주기적 동기화 및 만료 데이터 정리
    │   │   │   ├── ApiService.java             #   공공 API 호출 및 JSON 파싱 (병렬 처리)
    │   │   │   │
    │   │   │   ├── departure/                  #   [하위 도메인] 출국장 혼잡도
    │   │   │   │   ├── Departure.java          #     엔티티
    │   │   │   │   ├── DepartureController.java
    │   │   │   │   ├── DepartureService.java
    │   │   │   │   ├── dto/
    │   │   │   │   │   └── DepartureResDto.java
    │   │   │   │   └── repository/
    │   │   │   │       └── DepartureRepository.java
    │   │   │   │
    │   │   │   ├── parking/                    #   [하위 도메인] 주차장 현황
    │   │   │   │   ├── Parking.java            #     엔티티
    │   │   │   │   ├── ParkingController.java
    │   │   │   │   ├── ParkingService.java
    │   │   │   │   ├── ParkingRepository.java
    │   │   │   │   └── dto/
    │   │   │   │       └── ParkingResDto.java
    │   │   │   │
    │   │   │   ├── plane/                      #   [하위 도메인] 항공편 정보
    │   │   │   │   ├── Plane.java              #     엔티티
    │   │   │   │   ├── PlaneController.java
    │   │   │   │   ├── PlaneService.java       #     DB 저장/갱신 + Kafka 이벤트 발행
    │   │   │   │   ├── dto/
    │   │   │   │   │   ├── PlaneReqDto.java
    │   │   │   │   │   └── PlaneResDto.java
    │   │   │   │   └── repository/
    │   │   │   │       └── PlaneRepository.java
    │   │   │   │
    │   │   │   ├── search/                     #   [하위 도메인] Elasticsearch 항공편 검색
    │   │   │   │   ├── FlightDocument.java     #     ES 인덱스 매핑 (Nori 분석기, 자동완성)
    │   │   │   │   ├── FlightSearchController.java
    │   │   │   │   ├── FlightSearchService.java #    Fuzzy 검색 + 자동완성
    │   │   │   │   ├── FlightSearchRepository.java
    │   │   │   │   └── dto/
    │   │   │   │       └── FlightSearchResDto.java
    │   │   │   │
    │   │   │   ├── transit/                    #   [하위 도메인] 공항 이동시간 안내
    │   │   │   │   ├── TransitTimeController.java
    │   │   │   │   ├── TransitTimeService.java
    │   │   │   │   ├── TransitTimeDataLoader.java  # ApplicationRunner - 시작 시 JSON 데이터 로딩
    │   │   │   │   ├── arexTime/               #     공항철도(AREX) 소요시간
    │   │   │   │   │   ├── ArexTransitTime.java
    │   │   │   │   │   └── ArexTransitTimeRepository.java
    │   │   │   │   ├── parkingTime/            #     주차장 소요시간
    │   │   │   │   │   ├── ParkingTransitTime.java
    │   │   │   │   │   └── ParkingTransitTimeRepository.java
    │   │   │   │   └── dto/
    │   │   │   │       └── TransitTimeResDto.java
    │   │   │   │
    │   │   │   ├── weather/                    #   [하위 도메인] 날씨 정보
    │   │   │   │   ├── WeatherController.java
    │   │   │   │   ├── WeatherService.java
    │   │   │   │   └── dto/
    │   │   │   │       └── WeatherResDto.java
    │   │   │   │
    │   │   │   └── kafka/                      #   Kafka 이벤트 처리
    │   │   │       ├── KafkaConfig.java         #     토픽 정의 (indexing, changed, congestion)
    │   │   │       ├── consumer/
    │   │   │       │   ├── FlightIndexingConsumer.java    # plane.indexing → ES 인덱싱
    │   │   │       │   ├── PlaneStatusMailConsumer.java   # plane.changed → 항공편 변경 메일 발송
    │   │   │       │   └── CongestionAlertConsumer.java   # congestion.changed → 혼잡도 알림 메일
    │   │   │       └── message/
    │   │   │           ├── PlaneIndexingMessage.java      # ES 인덱싱용 메시지
    │   │   │           ├── PlaneChangedMessage.java       # 항공편 변경 알림 메시지
    │   │   │           └── CongestionMessage.java         # 혼잡도 알림 메시지
    │   │   │
    │   │   ├── messaging/                      # [도메인] 실시간 채팅 모듈
    │   │   │   ├── chat/                       #   [하위 도메인] 채팅 메시지
    │   │   │   │   ├── Chat.java               #     엔티티
    │   │   │   │   ├── ChatType.java           #     메시지 타입 enum
    │   │   │   │   ├── ChatController.java     #     STOMP 메시지 핸들러
    │   │   │   │   ├── ChatService.java
    │   │   │   │   ├── dto/
    │   │   │   │   │   ├── ChatReqDto.java
    │   │   │   │   │   └── ChatResDto.java
    │   │   │   │   └── repository/
    │   │   │   │       ├── ChatRepository.java
    │   │   │   │       ├── ChatRepositoryCustom.java     # QueryDSL 커스텀 인터페이스
    │   │   │   │       └── ChatRepositoryImpl.java       # QueryDSL 구현체
    │   │   │   │
    │   │   │   └── chatRoom/                   #   [하위 도메인] 채팅방 관리
    │   │   │       ├── ChatRoom.java           #     엔티티
    │   │   │       ├── ChatRoomController.java
    │   │   │       ├── ChatRoomService.java
    │   │   │       ├── SessionEventListener.java  # WebSocket 세션 이벤트 리스너 (접속자 수 관리)
    │   │   │       ├── dto/
    │   │   │       │   ├── ChatRoomReqDto.java
    │   │   │       │   └── ChatRoomResDto.java
    │   │   │       └── repository/
    │   │   │           ├── ChatRoomRepository.java
    │   │   │           ├── ChatRoomRepositoryCustom.java
    │   │   │           └── ChatRoomRepositoryImpl.java
    │   │   │
    │   │   ├── member/                         # [도메인] 회원 관리
    │   │   │   ├── Member.java                 #   엔티티
    │   │   │   ├── Role.java                   #   역할 enum
    │   │   │   ├── PlaneSubscription.java      #   관심 항공편 구독 엔티티
    │   │   │   ├── MemberController.java
    │   │   │   ├── MemberService.java
    │   │   │   ├── MemberRepository.java
    │   │   │   ├── PlaneSubscriptionRepository.java
    │   │   │   └── dto/
    │   │   │       ├── MemberReqDto.java
    │   │   │       └── MemberResDto.java
    │   │   │
    │   │   ├── auth/                           # [도메인] 인증/인가
    │   │   │   ├── jwt/                        #   JWT 처리
    │   │   │   │   ├── JwtFilter.java          #     요청별 JWT 검증 필터
    │   │   │   │   └── JwtUtil.java            #     JWT 생성/파싱 유틸
    │   │   │   ├── oauth/                      #   OAuth2 소셜 로그인
    │   │   │   │   ├── OAuth2Response.java     #     응답 인터페이스
    │   │   │   │   ├── GoogleResponse.java     #     Google 응답 구현체
    │   │   │   │   ├── KakaoResponse.java      #     Kakao 응답 구현체
    │   │   │   │   ├── CustomOAuth2User.java   #     인증 사용자 객체
    │   │   │   │   ├── CustomOAuth2UserService.java  # 사용자 정보 로딩
    │   │   │   │   └── CustomSuccessHandler.java     # 로그인 성공 후 JWT 발급
    │   │   │   └── token/                      #   토큰 관리 (Refresh Token)
    │   │   │       ├── RefreshToken.java        #     엔티티
    │   │   │       ├── RefreshTokenRepository.java
    │   │   │       ├── ReissueController.java   #     토큰 재발급 API
    │   │   │       └── ReissueService.java
    │   │   │
    │   │   ├── config/                         # [설정] 전역 설정
    │   │   │   ├── AppConfig.java              #   RestTemplate 등 공통 Bean
    │   │   │   ├── SecurityConfig.java         #   Spring Security 설정
    │   │   │   ├── WebCorsConfig.java          #   CORS 설정
    │   │   │   ├── WebsocketConfig.java        #   STOMP WebSocket 설정
    │   │   │   ├── QuerydslConfig.java         #   QueryDSL JPAQueryFactory Bean
    │   │   │   └── ElasticsearchConfig.java    #   ES 클라이언트 설정
    │   │   │
    │   │   ├── common/                         # [공통] 유틸리티
    │   │   │   ├── BaseTime.java               #   생성/수정 시간 자동 관리 (Auditing)
    │   │   │   ├── BaseBatch.java              #   배치 작업 기본 엔티티
    │   │   │   ├── CookieUtil.java             #   쿠키 생성 유틸
    │   │   │   └── SnowflakeIdGenerator.java   #   Snowflake 분산 ID 생성기
    │   │   │
    │   │   ├── exception/                      # [예외] 전역 예외 처리
    │   │   │   ├── ErrorCode.java              #   에러 코드 enum
    │   │   │   ├── ChatException.java          #   커스텀 예외
    │   │   │   ├── CustomExceptionHandler.java #   @ControllerAdvice 전역 핸들러
    │   │   │   └── ErrorDto.java               #   에러 응답 DTO
    │   │   │
    │   │   ├── annotation/                     # [어노테이션] 커스텀 어노테이션
    │   │   │   └── ReadOnlyTransaction.java    #   읽기 전용 트랜잭션
    │   │   │
    │   │   └── aspect/                         # [AOP] 관점 지향
    │   │       └── PlaneDataAspect.java        #   API 호출 실행 시간 측정
    │   │
    │   └── resources/
    │       ├── application.properties          # 애플리케이션 설정
    │       ├── data/
    │       │   ├── arex-transit-time.json       # 공항철도 소요시간 초기 데이터
    │       │   └── parking-transit-time.json    # 주차장 소요시간 초기 데이터
    │       ├── elasticsearch/
    │       │   └── flight-index-settings.json   # ES 인덱스 설정 (Nori 분석기 등)
    │       └── templates/
    │           └── mail/
    │               ├── flight-status-changed.html  # 항공편 변경 알림 메일 템플릿
    │               └── congestion-alert.html       # 혼잡도 알림 메일 템플릿
    │
    └── test/
        └── java/com/example/chat/
            ├── ChatApplicationTests.java       # 컨텍스트 로드 테스트
            └── ApiServiceTest.java             # API 호출 단위 테스트
```

## 핵심 데이터 흐름

### 항공편 데이터 동기화 파이프라인

```
[인천공항 공공 API]
       │
       ▼
[AirportScheduler] ── 어제, 오늘, 내일, 이틀 뒤 데이터를 가져옵니다. ( 어제 : 10분마다 , 오늘 + 내일 : 2분마다 , 이틀 뒤 : 30분마다 )
       │
       ▼
[ApiService.getApiPlane()]
       │
       ▼
[PlaneService.upsertPlaneData()] ── DB 저장/갱신
       │
       ├──→ Kafka: airport.plane.indexing ──→ [FlightIndexingConsumer] ──→ Elasticsearch 인덱싱
       │
       └──→ Kafka: airport.plane.changed  ──→ [PlaneStatusMailConsumer] ──→ 구독 회원 메일 알림
```

### 실시간 채팅 흐름

```
[클라이언트] ── STOMP CONNECT ──→ [WebsocketConfig (SockJS)]
       │                                    │
       │                          [SessionEventListener]
       │                          (접속자 수 추적/브로드캐스트)
       │
       ├── SEND /pub/message ──→ [ChatController] ──→ [ChatService] ──→ DB 저장
       │                                │
       │                                ▼
       └── SUBSCRIBE /sub/room/{id} ← 브로드캐스트 (/sub/room/{id})
```

## Kafka 토픽 구성

| 토픽명 | 파티션 | 보존 기간 | 용도 |
|--------|--------|-----------|------|
| `airport.plane.indexing` | 3 | 7일 | 항공편 INSERT/UPDATE → ES 인덱싱 |
| `airport.plane.changed` | 3 | 7일 | 항공편 변경 → 구독 회원 메일 알림 |
| `airport.congestion.changed` | 3 | 3일 | 출국장 혼잡도 변화 → 알림 메일 |

## 도메인 패키지 구조 패턴

각 도메인은 다음 패턴을 따른다:

```
{domain}/
├── {Entity}.java             # JPA 엔티티
├── {Entity}Controller.java   # REST 컨트롤러
├── {Entity}Service.java      # 비즈니스 로직
├── dto/
│   ├── {Entity}ReqDto.java   # 요청 DTO
│   └── {Entity}ResDto.java   # 응답 DTO
└── repository/
    ├── {Entity}Repository.java        # JPA Repository
    ├── {Entity}RepositoryCustom.java  # QueryDSL 커스텀 인터페이스 (필요 시)
    └── {Entity}RepositoryImpl.java    # QueryDSL 구현체 (필요 시)
```

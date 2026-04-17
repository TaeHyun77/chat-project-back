### 인천공항 정보 및 채팅 프로젝트
---
인천공항 출국에 필요한 항공편·출국장·주차장·날씨 정보를 실시간으로 제공하고, 채팅을 통해 사용자 간 정보 교환을 지원하는 풀스택 프로젝트입니다.<br><br>

개발 과정 블로그

https://velog.io/@ayeah77/series/%EA%B3%B5%ED%95%AD%EC%A0%95%EB%B3%B4-%EC%B1%84%ED%8C%85-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8<br><br>


### 기술 스택
---
- Framework : Spring Boot 3.4, Spring Security, Spring WebSocket (STOMP)
- Frontend : React.js, JavaScript
- ORM / Query : JPA, QueryDSL 5.0
- Database : MySQL 8.0
- Messaging : Apache Kafka 3.7
- Search Engine : Elasticsearch (Nori Analyzer)
- Authentication : OAuth2 (Google, Kakao), JWT
- Mail : Spring Mail, Thymeleaf
- Infrastructure : Docker Compose, AWS EC2, Nginx<br><br>


### 주요 기능
---
**항공편 정보**
- 인천공항 공공 API를 통해 어제 ~ 모레까지 출발 항공편을 자동 동기화
- 날짜별 차등 주기: 오늘·내일(2분) / 어제(10분) / 모레(30분)
- Elasticsearch Fuzzy 검색 + 한글 자동완성 지원 (Nori 분석기)
- 매 자정 이틀 전 완료 항공편 자동 정리

**항공편 구독 & 알림**
- 관심 항공편 구독 시, 상태 변경(지연·결항·게이트 변경 등)을 메일로 알림
- Kafka 이벤트 기반 비동기 처리 (airport.plane.changed)

**출국장 혼잡도**
- 터미널별 출국장 혼잡도 실시간 조회
- 혼잡도 변화 시 출발 임박(6시간 이내) 구독 회원에게 메일 알림
- 매 자정 전일 데이터 자동 정리

**실시간 채팅**
- WebSocket + STOMP 프로토콜 기반 채팅방 생성/참여
- 접속자 수 실시간 추적 및 브로드캐스트
- QueryDSL 기반 메시지 조회

**부가 기능**
- 주차장 현황: 층별 주차 가능 대수 실시간 조회
- 날씨 정보: 도착 공항(20개 주요 공항) 날씨 조회 (Open-Meteo, 10분 캐싱)
- 이동시간: 공항철도(AREX) · 주차장별 소요시간 안내

**인증**
- OAuth2 소셜 로그인 (Google, Kakao)
- JWT Access Token + Refresh Token 기반 인증
- 자동 토큰 재발급<br><br>

### 개발 포인트
---
- JPA Join Fetch로 N+1 문제 해결, 조회 시간 713ms → 263ms (약 63% 단축)
- Slice 기반 페이징으로 불필요한 전체 데이터 로딩 방지
- set_option/commit 부수 쿼리로 인한 DB 오버헤드를 식별하고, 불필요한 트랜잭션 제거
- JDBC Batch Insert(batch_size=100)로 대량 항공편 데이터 효율적 저장/갱신
- JPQL → QueryDSL 전환으로 타입 안정성과 가독성 개선
- Kafka 이벤트 기반 비동기 처리로 항공편 인덱싱과 메일 알림을 서비스 로직에서 분리
- Elasticsearch Nori 분석기 + Fuzzy 검색으로 한글 항공편 검색 정확도 향상
- Docker Compose 기반 인프라 구성 및 AWS EC2 컨테이너 배포
- HTTPS 전환 (OAuth2 요구사항) 및 React 정적 빌드 + Nginx 서빙으로 EC2 메모리 최적화

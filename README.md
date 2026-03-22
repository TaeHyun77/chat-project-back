## 프로젝트 설명
인천공항을 통한 출국 시 필요한 출국장 혼잡도와 항공편 정보를 제공하고, 채팅 기능을 제공하여 사용자들끼리 정보를 얻을 수 있도록 하는 프로젝트입니다.<br><br>

개발 과정 블로그<br>
https://velog.io/@ayeah77/series/%EA%B3%B5%ED%95%AD%EC%A0%95%EB%B3%B4-%EC%B1%84%ED%8C%85-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8<br><br>

## 기술 스택
Backend : SpringBoot, Spring Data JPA

Frontend : React.js, JavaScript

Database : MySQL

ETC : Websocket, STOMP

CI/CD : Docker, Aws Ec2<br><br>

## 아키텍처
<p align="center">
<img width="657" height="520" alt="스크린샷 2026-03-07 오후 8 26 11" src="https://github.com/user-attachments/assets/7cea47f3-d3bf-4571-a666-476c9163b7e7" /><br><br>

## 기능
- Oauth + JWT를 사용한 구글 로그인

- 인천공항 출국장 혼잡도 및 항공편 정보 제공<br>
  ( 공공 API 데이터를 사용하여, 출국장 정보는 오늘 기준 2일간 3분 간격으로, 항공편 정보는 오늘 기준 3일간 1분 간격으로 갱신 및 제공합니다. )<br>

- WebSocket과 STOMP를 활용한 실시간 채팅 기능<br><br>

## 개발 사항
- JPA Join Fetch를 적용하여 연관 관계 조회 시 발생하는 N+1 문제를 방지하고, 조회 시간을 713ms에서 263ms로 약 63% 단축하였습니다.

- JPA 페이징을 적용하여 항공편 데이터 조회 시 불필요한 전체 데이터 로딩을 방지하였습니다.

- set_option/commit 부수 쿼리로 인한 DB 라운드트립 오버헤드를 식별하고, 불필요한 트랜잭션을 제거하여 최적화하였습니다.

- 항공편/혼잡도 API 호출을 CompletableFuture 기반 병렬 처리로 전환하여 데이터 수집 속도를 2배 이상 개선하였으며, JDBC Batch Insert를 적용하여 수천 건의 데이터를 효율적으로 저장 및 갱신할 수 있도록 하였습니다. 현재 항공편 데이터는 2천 건 미만으로 성능 개선 효과가 크지 않으나, 향후 데이터 규모 확장을 고려하여 적용하였습니다.

- 조회 쿼리를 JPQL에서 QueryDSL로 전환하여 타입 안정성과 가독성을 높이고 유지보수성을 개선하였습니다.

- 불필요한 트랜잭션을 제거하고 트랜잭션 범위를 최소화하여 불필요한 자원 점유를 줄였습니다.

- Docker와 AWS EC2를 활용한 컨테이너 기반 배포 및 도메인 연결

- EC2 환경에서 React 개발 서버 실행 시 발생하던 메모리 부족 문제를 해결하기 위해, 빌드된 정적 파일을 Nginx로 서빙하는 방식으로 전환하였습니다.

- Google OAuth 적용을 위해 커스텀 도메인을 연결하고 HTTP에서 HTTPS로 전환하였습니다. ( OAuth는 퍼블릭 IP 및 HTTP 환경을 지원하지 않기 때문 )

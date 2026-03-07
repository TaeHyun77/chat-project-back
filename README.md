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
<img width="600" height="300" alt="Image" src="https://github.com/user-attachments/assets/143873f2-831a-46d2-b4df-1fab927e6c15" />
<br><br>

## 기능
- Oauth + JWT를 사용한 구글 로그인

- 인천공항 출국장 혼잡도 및 항공편 정보 제공<br>
  ( 공공 API 데이터를 사용하여, 출국장 정보는 오늘 기준 2일간 3분 간격으로, 항공편 정보는 오늘 기준 3일간 1분 간격으로 갱신 및 제공합니다. )<br>

- WebSocket과 STOMP를 활용한 실시간 채팅 기능

- Docker와 AWS EC2를 이용한 배포 및 도메인 적용<br><br>

## 개선 사항
- JPA Join Fetch를 이용한 연관 관계 조회 최적화를 통해 조회 시간이 713ms에서 263ms로 약 63%의 속도 개선을 하였습니다.<br>

- JPA 페이징을 적용하여 항공편 데이터 조회 시, 불필요한 데이터 조회를 방지하였습니다.
  
- 조회 쿼리를 JPQL에서 Query DSL로 변경<br>
  ( 타입 안정성과 가독성을 향상시키고 유지보수성을 높히기 위함입니다. )

- 불필요한 트랜잭션 사용을 제거하고, 트랜잭션 범위를 최소화하는 방향으로 로직을 개선했습니다.

- React 개발 서버 실행 시 발생하던 메모리 부족 문제를 해결하기 위해, Nginx를 통해 빌드된 정적 파일을 서빙하는 방식으로 변경
  
- Oauth 적용을 위해 도메인을 적용하고 Http 프로토콜을 Https 프로토콜로 변경<br>
  ( Oauth는 퍼블릭 ip와 http를 지원하지 않기 때문 )<br><br>

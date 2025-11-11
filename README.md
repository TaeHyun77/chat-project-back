## 프로젝트 설명
인천공항을 통한 출국 시 필요한 출국장 혼잡도와 항공편 정보를 제공하고, 채팅 기능을 제공하여 사용자들끼리 정보를 얻을 수 있도록 하는 프로젝트입니다.<br><br>

블로그 : https://velog.io/@ayeah77/series/%EA%B3%B5%ED%95%AD%EC%A0%95%EB%B3%B4-%EC%B1%84%ED%8C%85-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8<br><br>

## 기술 스택
Backend : SpringBoot, Spring Data Jpa

Frontend : React.js, JavaScript

Database : MySQL , Redis

ETC : Websocket, STOMP

CI/CD : Docker, Aws Ec2<br><br>

## 아키텍처
<p align="center">
<img width="594" height="260" alt="Image" src="https://github.com/user-attachments/assets/f6b73474-5c23-4902-a15a-3fd24f9d7f04" />
<br><br>

## 기능
- Oauth를 통한 구글 로그인

- 출국장 혼잡도 및 항공편 정보 확인 가능<br>
  ( 출국장 데이터는 10분 간격으로 최근 2일, 항공편 데이터는 5분 간격으로 최근 3일 기준으로 갱신되어 제공됩니다. )<br>
  ( 외부 API를 호출하여 갱신 및 저장 )

- websocket과 STOMP를 활용한 채팅 기능

- Docker와 AWS Ec2를 이용한 배포 진행

- 도메인과 Https 프로토콜 적용<br><br>

## 개선 사항
- 기존 데이터 조회 시 DB 대신 Redis를 사용하여 응답 속도 개선<br>
  ( Redis 캐싱을 통해 API 응답 속도롤 약 110% 개선 )

- JPA Join Fetch를 이용한 연관 관계 조회 최적화를 통해 조회 시간이 713ms에서 263ms로 약 63%의 속도 개선을 하였습니다.<br>
  ( 불필요한 쿼리를 유발하는 N+1 문제를 해결 )
  
- 조회 쿼리를 JPQL에서 Query DSL로 변경<br>
  ( 타입 안정성과 가독성을 향상시키고 유지보수성을 높히기 위함입니다. )

- React 개발 서버 실행 시 발생하던 메모리 부족 문제를 해결하기 위해, Nginx를 통해 빌드된 정적 파일을 서빙하는 방식으로 변경
  
- Oauth 적용을 위해 도메인을 적용하고 Http 프로토콜을 Https 프로토콜로 변경<br>
  ( Oauth는 퍼블릭 ip와 http를 지원하지 않기 때문 )<br><br>

## 페이지 

**[ 전체 홈 페이지 ]**
<br><br>
<img src="https://github.com/user-attachments/assets/56d843b5-bd84-4541-9946-b91ec4cc5927" width=700 align="center"><br><br><br>

**[ 메인 페이지 - 출국장 정보 ]**
<br><br>

각 출국장의 혼잡도를 수치와 그래프를 통해 나타내였습니다. <br>
<img src="https://github.com/user-attachments/assets/d09a03c8-ffb3-4e9e-9b24-3e22a7496a70" width=600 align="center"><br><br><br>

**[ 메인 페이지 - 항공편 정보 ]**
<br><br>

3일 내의 출발 예정인 항공편만 나타내며, 지연되는 항공편은 따로 표시를 해두었습니다.<br><br>
<img src="https://github.com/user-attachments/assets/064eeb93-53fd-4704-a56c-1ecc8fa83905" width=600 align="center"><br><br><br>

**[ 마이 페이지 - 정보 수정 및 생성한 채팅방 정보 ]**
<br><br>
<img src="https://github.com/user-attachments/assets/dfb0ff8c-b9b8-4856-9436-c7cae7594c6f" width=600 align="center"><br><br><br>

**[ 채팅방 목록 페이지 ]**
<br><br>
<img src="https://github.com/user-attachments/assets/462d2e24-2c32-4ef8-8bd1-95b436482fb0" width=600 align="center"><br><br><br>

**[ 채방방 페이지 ]**
<br><br>

모든 사용자들이 접속할 수 있는 오픈 채팅방이며 채팅방 인원수와 접속, 퇴장 여부 기능을 제공합니다.
<img src="https://github.com/user-attachments/assets/0778a890-b2a5-4c64-946b-9879853aaabb" width=600 align="center"><br><br><br>

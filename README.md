## 프로젝트 설명
인천공항을 통한 출국 시 필요한 출국장 혼잡도와 항공편 정보를 제공하고, 채팅 기능을 통해 사용자들끼리 편의성과 정보를 얻을 수 있도록 하는 프로젝트입니다.<br><br>

## 기술 스택
Backend : SpringBoot, Spring Data Jpa<br><br>
Frontend : React.js, JavaScript<br><br>
Database : MySQL , Redis<br><br>
CI/CD : Docker, Aws Ec2<br><br><br>

## 아키텍처
<p align="center">
<img width="600" height="550" alt="Image" src="https://github.com/user-attachments/assets/3d3c93c2-4c25-484e-88b0-7be149ba179d" />
<br><br>

## 기능
- Oauth를 통한 구글 로그인<br><br>
- 스케줄링을 통한 인천공항 API 저장 및 업데이트 <br><br>
  ( 출국장 데이터는 10분 간격으로 최근 2일, 항공편 데이터는 5분 간격으로 최근 3일 기준으로 제공 )<br><br>
- websocket과 STOMP를 이용한 채팅 구현<br><br>
- 도메인 적용과 Https 프로토콜 변경<br><br><br>

## 개선 사항
- 업데이트를 위해 기존 데이터 조회 시 DB 대신 Redis를 사용하여 응답 속도 개선<br>
  ( Redis 캐싱을 통해 API 응답 속도롤 약 2배 개선 -> 110% 개선 )<br><br>
- JPA Join Fetch를 이용하여 연관 관계 조회 최적화 <br>
  ( 불필요한 쿼리를 줄이고 JPA의 N+1 문제를 방지하기 위함입니다. )<br><br>
- 조회 쿼리를 JPQL에서 Query DSL로 변경 <br>
  ( 타입 안정성과 가독성을 향상시키기 위해서이고 동적 쿼리 작성이 용이하기 때문에 변경했습니다. )<br><br>
- 여러 데이터를 반복문을 통해 save()하는 것에서 saveAll() 방식으로 변경 <br>
  ( 트랜잭션 처리 효율을 높이고 불필요한 DB 접근을 최소화 )<br><br>
- Oauth 적용을 위해 도메인을 적용하고 Http 프로토콜을 Https 프로토콜로 변경 <br>
  ( Oauth는 퍼블릭 ip와 http를 지원하지 않기 때문입니다. )<br><br>

## 페이지 
<p>전체 홈 페이지</p>
<img src="https://github.com/user-attachments/assets/56d843b5-bd84-4541-9946-b91ec4cc5927" width=700 align="center"><br><br><br>

<p>메인 페이지 - 출국장 정보</p>
<img src="https://github.com/user-attachments/assets/d09a03c8-ffb3-4e9e-9b24-3e22a7496a70" width=600 align="center"><br><br><br>

<p>메인 페이지 - 항공편 정보</p> 
<img src="https://github.com/user-attachments/assets/064eeb93-53fd-4704-a56c-1ecc8fa83905" width=600 align="center"><br><br><br>

<p>마이 페이지 - 정보 수정 및 생성한 채팅방 정보</p> 
<img src="https://github.com/user-attachments/assets/dfb0ff8c-b9b8-4856-9436-c7cae7594c6f" width=600 align="center"><br><br><br>

<p>채팅방 목록 페이지</p> 
<img src="https://github.com/user-attachments/assets/462d2e24-2c32-4ef8-8bd1-95b436482fb0" width=600 align="center"><br><br><br>

<p>채방방 페이지</p> 
<img src="https://github.com/user-attachments/assets/0778a890-b2a5-4c64-946b-9879853aaabb" width=600 align="center"><br><br><br>

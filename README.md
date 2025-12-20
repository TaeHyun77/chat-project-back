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

- React 개발 서버 실행 시 발생하던 메모리 부족 문제를 해결하기 위해, Nginx를 통해 빌드된 정적 파일을 서빙하는 방식으로 변경
  
- Oauth 적용을 위해 도메인을 적용하고 Http 프로토콜을 Https 프로토콜로 변경<br>
  ( Oauth는 퍼블릭 ip와 http를 지원하지 않기 때문 )<br><br>

## 페이지 

**[ 전체 홈 페이지 ]**
<br><br>
<img width="1000" height="800" alt="Image" src="https://github.com/user-attachments/assets/7e829420-53d3-4626-ad4c-308436d2ab9f" /><br><br><br>

**[ 메인 페이지 - 출국장 정보 ]**
<br><br>

각 출국장의 혼잡도를 수치와 그래프를 통해 나타내였습니다. <br>
<img width="1000" height="500" alt="Image" src="https://github.com/user-attachments/assets/e15285c2-2614-46e3-b62d-436de9997c71" /><br><br><br>

**[ 메인 페이지 - 항공편 정보 ]**
<br><br>

3일 내의 출발 예정인 항공편만 나타내며, 지연되는 항공편은 따로 표시를 해두었습니다.<br><br>
<img width="1000" height="500" alt="Image" src="https://github.com/user-attachments/assets/684b9d74-8985-4208-9211-12508cc4f296" /><br><br><br>

**[ 메인 페이지 - 화면의 너비가 줄었을 때의 항공편 정보 ]**
<br><br>

<img width="1000" height="600" alt="Image" src="https://github.com/user-attachments/assets/61992ab7-67a7-4aa3-a117-5dc993c83233" />br><br><br>

**[ 마이 페이지 - 정보 수정 및 생성한 채팅방 정보 ]**
<br><br>
<img src="https://github.com/user-attachments/assets/dfb0ff8c-b9b8-4856-9436-c7cae7594c6f" width=600 align="center"><br><br><br>

**[ 채팅방 목록 페이지 ]**
<br><br>
<img width="1000" height="750" alt="Image" src="https://github.com/user-attachments/assets/f941fe00-7a61-4081-93ed-b0fb50b53ea4" /><br><br><br>

**[ 채방방 페이지 ]**
<br><br>

로그인한 모든 사용자가 참여할 수 있는 오픈 채팅방으로, 실시간 채팅과 접속자 수, 입·퇴장 상태를 제공합니다.<br><br>
<img width="1000" height="750" alt="Image" src="https://github.com/user-attachments/assets/1950b072-ff45-428f-9181-38f10e9eb922" />
<img width="1000" height="750" alt="Image" src="https://github.com/user-attachments/assets/813ea2a4-9891-4594-a8cc-8b99163a38bf" />
<br><br><br>

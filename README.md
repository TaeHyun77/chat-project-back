## chat-project
인천 공항 정보 제공 및 채팅 프로젝트

## 목적
인천공항을 통한 출국 시 필요한 출국장 혼잡도와 항공편 정보를 한 번에 제공하고, <br>
출국자들 간 실시간 소통이 가능한 채팅 기능을 더해 사용자의 편의성과 정보 공유를 높이기 위함입니다.

## 사용 기술
BackEnd : SpringBoot, Spring Data Jpa<br>
FrontEnd : React.js<br>
DataBase : MySQL , Redis<br>
CI/CD : Docker, Aws
<br>
## 기능
- Oauth를 통한 구글 로그인<br>
- 스케줄링을 통한 인천공항 API 저장 및 업데이트 ( 출국장 데이터는 10분 간격으로 최근 2일, 항공편 데이터는 5분 간격으로 최근 3일 기준으로 제공 )<br>
- websocket과 STOMP를 이용한 채팅 구현<br>
- 도메인 적용과 Https 프로토콜 변경<br>

## 개선 사항

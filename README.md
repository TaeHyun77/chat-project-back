### 프로젝트
---
출국 시 필요한 항공편 상태와 출국장 혼잡도 및 기타 정보를 한 곳에서 실시간으로 확인할 수 있으며, 항공편 검색 및 구독을 통한 알림 기능과 사용자 간 소통을 위한 채팅 기능을 제공하는 통합 공항 정보 서비스<br><br>

### 아키텍처
---
<img width="800" height="400" alt="image" src="https://github.com/user-attachments/assets/391915e3-5910-42b4-9b7e-ebda193f56c5" /><br><br>

### 사용 기술
---
- Backend : Spring Boot, Spring Data JPA, Java
- Frontend : React, JavaScript
- Database : MySQL (RDS)
- Messaging : Kafka
- Search : Elasticsearch
- Infrastructure : Docker, AWS EC2<br><br>

### 구현 및 기능
---
- 출국장 혼잡도(오늘/내일) 및 항공편 정보(어제~모레)를 제공하여 공항 이용 전반에 대한 실시간 정보
- Elasticsearch를 활용해 항공편 검색 기능
- 항공편 구독 기능을 통해 관심 항공편의 상태 변경(지연, 결항 등) 발생 시 메일 알림 기능
- 실시간 주차장 현황과 함께 체크인 카운터, 공항철도, 주차장까지의 예상 소요 시간 등 부가 정보 제공
- WebSocket과 STOMP 기반의 오픈 채팅 기능

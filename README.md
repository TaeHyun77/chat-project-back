### 프로젝트
---
출국 시 필요한 항공편 상태와 출국장 혼잡도 및 기타 정보를 한 곳에서 실시간으로 확인할 수 있으며, 항공편 검색 및 구독을 통한 알림 기능과 사용자 간 소통을 위한 채팅 기능을 제공하는 통합 공항 정보 서비스<br><br>

### 아키텍처
---
<p align="center">
<img width="1000" height="500" alt="image" src="https://github.com/user-attachments/assets/391915e3-5910-42b4-9b7e-ebda193f56c5" /></p><br><br>

### 기술 스택
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

### 트러블 슈팅 및 개선
---
- 대량 데이터 저장 시 성능 개선을 위해 Batch Insert/Update를 적용하였으며, 현재 프로젝트의 데이터 규모에서는 성능 향상이 크지 않았지만 향후 확장성을 고려하여 도입하였습니다.

- JPA의 Fetch Join을 적용하여 N+1 문제를 해결하고, 연관 관계 조회 성능을 약 63% 개선하였습니다.

- 제한된 EC2 환경(1 Core, 1GB Memory)에서 Docker 컨테이너 실행 시 React 서버의 메모리 사용량으로 인해 컨테이너가 정상적으로 실행되지 않는 문제 발생하였습니다.

  스왑 메모리(2GB)를 추가했으나 메모리 부족 문제가 지속되어, React 애플리케이션을 빌드하여 정적 파일로 변환하고 Nginx에서 서빙하도록 구조를 변경하여, 서버 메모리 사용량을 줄이고 안정적으로 서비스를 운영할 수 있도록 개선하였습니다.

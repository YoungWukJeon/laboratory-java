# Apache Kafka를 이용한 채팅 애플리케이션

## 빌드 명령
- gradle jar

## 실행 명령
1. infra/docker-compose.yaml을 실행(docker-compose up -d)
2. 서버 실행(java -jar build/libs/chatting-application.jar -t server -h localhost -p 8888)
3. 클라이언트 실행(java -jar build/libs/chatting-application.jar -t client -h localhost -p 8888)

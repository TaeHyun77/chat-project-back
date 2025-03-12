FROM amazoncorretto:17-alpine

WORKDIR /app

COPY build/libs/chat-0.0.1-SNAPSHOT.jar chat.jar

CMD ["java", "-jar", "chat.jar"]

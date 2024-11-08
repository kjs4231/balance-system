FROM openjdk:17-jdk-slim
COPY build/libs/*.jar /app/batch-service.jar
ENTRYPOINT ["java", "-jar", "/app/batch-service.jar"]
EXPOSE 9003

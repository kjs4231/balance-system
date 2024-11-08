FROM openjdk:17-jdk-slim
COPY build/libs/*.jar /app/gateway-service.jar
ENTRYPOINT ["java", "-jar", "/app/gateway-service.jar"]
EXPOSE 8000

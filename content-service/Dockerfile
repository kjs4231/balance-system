FROM openjdk:17-jdk-slim
COPY build/libs/*.jar /app/content-service.jar
ENTRYPOINT ["java", "-jar", "/app/content-service.jar"]
EXPOSE 9007

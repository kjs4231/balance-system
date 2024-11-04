FROM openjdk:17-jdk-slim
COPY build/libs/*.jar /app/global.jar
ENTRYPOINT ["java", "-jar", "/app/global.jar"]
EXPOSE 9001

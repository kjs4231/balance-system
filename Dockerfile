# 베이스 이미지로 OpenJDK 17을 사용
FROM openjdk:17-jdk-slim

# 빌드된 JAR 파일을 컨테이너 내로 복사
COPY build/libs/*.jar /app/myapp.jar

# JAR 파일을 실행하는 명령어
ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
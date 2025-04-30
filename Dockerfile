### 배포 환경
#FROM openjdk:21-jdk-slim
#
#ARG JAR_FILE=./build/libs/*.jar
#
#COPY ${JAR_FILE} /app.jar
#
#ENTRYPOINT ["java", "-jar", "/app.jar" ]

## 개발 환경
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

ENTRYPOINT ["./gradlew", "bootRun"]
### 배포 환경
#FROM openjdk:21-jdk-slim
#
#ARG JAR_FILE=./build/libs/*.jar
#
#COPY ${JAR_FILE} /app.jar
#
#ENTRYPOINT ["java", "-jar", "/app.jar" ]

## 개발 환경
# 1단계: builder
FROM gradle:8.4.0-jdk21 AS builder

WORKDIR /app

COPY . .

RUN gradle bootJar --no-daemon

# 2단계: runtime
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

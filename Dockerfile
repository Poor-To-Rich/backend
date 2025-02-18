# 배포 환경
FROM openjdk:21-jdk

ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar" ]

# 개발 환경
#FROM openjdk:21-jdk
#
#WORKDIR /app
#
#COPY . .
#
#RUN chmod +x gradlew
#
#ENTRYPOINT [ "./gradlew", "bootRun" ]
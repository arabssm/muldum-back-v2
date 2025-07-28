FROM openjdk:21-jdk-slim
LABEL authors="simhyeonjin"

COPY wait-for-it.sh /wait-for-it.sh
COPY ./build/libs/*.jar app.jar

RUN chmod +x /wait-for-it.sh

EXPOSE 8080
ENTRYPOINT ["/wait-for-it.sh", "postgres:5432", "--timeout=60", "--strict", "--", "java", "-jar", "/app.jar"]

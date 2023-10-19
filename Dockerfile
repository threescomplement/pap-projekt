FROM openjdk:22-slim-bullseye
VOLUME /tmp
COPY build/libs/*SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
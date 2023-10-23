FROM eclipse-temurin:17-jdk-alpine
MAINTAINER barrierelos.ch
VOLUME /var/lib/barrierelos/backend
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

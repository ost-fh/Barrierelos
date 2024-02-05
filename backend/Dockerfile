FROM openjdk:17-jdk-alpine as build
COPY . /
RUN export GRADLE_USER_HOME=`pwd`/.gradle
RUN ./gradlew clean
RUN ./gradlew assemble

FROM eclipse-temurin:17-jdk-alpine
COPY --from=build build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

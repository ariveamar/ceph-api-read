FROM gradle:6.9.0-jdk8 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test

FROM openjdk:11.0.11-jre-slim

ENV TZ = Asia/Jakarta
EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/ceph-storage-read-api-1.0.0.jar /app/application.jar

ENTRYPOINT ["java", "-Duser.timezone=ID", "-jar","/app/application.jar"]

# syntax=docker/dockerfile:experimental
FROM gradle:7-jdk17-alpine as gradle7

USER root

COPY gradle.properties /home/gradle/gradle.properties
COPY build.gradle /home/gradle/build.gradle
COPY gradle-scripts/settings.geography.gradle /home/gradle/settings.gradle
COPY supplychain-common/ /home/gradle/supplychain-common/
COPY supplychain-geography-service-client/ /home/gradle/supplychain-geography-service-client/
COPY supplychain-geography-service/ /home/gradle/supplychain-geography-service/

RUN --mount=type=cache,target=/home/gradle/.gradle gradle --no-daemon clean build && cp /home/gradle/supplychain-geography-service/build/libs/geography-service.war /opt && \
    rm -rf /home/gradle/supplychain*  

FROM tomcat:10-jdk17-openjdk-slim

RUN apt-get update && apt-get install -y curl

RUN rm -rf /usr/local/tomcat/webapps/ROOT

COPY --from=gradle7 /opt/geography-service.war /usr/local/tomcat/webapps/ROOT.war
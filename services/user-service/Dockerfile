# syntax=docker/dockerfile:1.7
ARG MAVEN_IMAGE=public.ecr.aws/docker/library/maven:3.9.9-eclipse-temurin-17
ARG RUNTIME_IMAGE=public.ecr.aws/docker/library/eclipse-temurin:17-jre
ARG MAVEN_CLI_OPTS=-B -ntp -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.wagon.http.retryHandler.requestSentEnabled=true
ARG MAVEN_MIRROR_URL=

FROM ${MAVEN_IMAGE} AS build

ARG MAVEN_CLI_OPTS
ARG MAVEN_MIRROR_URL

WORKDIR /workspace

COPY pom.xml ./
COPY services/pom.xml services/pom.xml
COPY services/common/pom.xml services/common/pom.xml
COPY services/common-messaging/pom.xml services/common-messaging/pom.xml
COPY services/gateway-service/pom.xml services/gateway-service/pom.xml
COPY services/user-service/pom.xml services/user-service/pom.xml
COPY services/activity-service/pom.xml services/activity-service/pom.xml
COPY services/announcement-service/pom.xml services/announcement-service/pom.xml
COPY services/feedback-service/pom.xml services/feedback-service/pom.xml
COPY services/monitor-service/pom.xml services/monitor-service/pom.xml
COPY services/mcp-service/pom.xml services/mcp-service/pom.xml

RUN if [ -n "$MAVEN_MIRROR_URL" ]; then \
      printf '%s\n' \
        '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">' \
        '  <mirrors>' \
        '    <mirror>' \
        '      <id>custom-mirror</id>' \
        '      <mirrorOf>*</mirrorOf>' \
        "      <url>$MAVEN_MIRROR_URL</url>" \
        '    </mirror>' \
        '  </mirrors>' \
        '</settings>' > /tmp/maven-settings.xml; \
    fi

RUN --mount=type=cache,target=/root/.m2 sh -lc 'SETTINGS=""; if [ -n "$MAVEN_MIRROR_URL" ]; then SETTINGS="--settings /tmp/maven-settings.xml"; fi; mvn $MAVEN_CLI_OPTS $SETTINGS -pl services/user-service -am dependency:go-offline'

COPY services/common services/common
COPY services/common-messaging services/common-messaging
COPY services/user-service services/user-service

RUN --mount=type=cache,target=/root/.m2 sh -lc 'SETTINGS=""; if [ -n "$MAVEN_MIRROR_URL" ]; then SETTINGS="--settings /tmp/maven-settings.xml"; fi; mvn $MAVEN_CLI_OPTS $SETTINGS -pl services/user-service -am clean package -DskipTests'

FROM ${RUNTIME_IMAGE}

WORKDIR /app
ENV APP_LOG_FILE=/app/logs/debug.log
ENV TZ=Asia/Shanghai
ENV JAVA_TOOL_OPTIONS=-Duser.timezone=Asia/Shanghai

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /app/logs

COPY --from=build /workspace/services/user-service/target/user-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

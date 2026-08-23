# syntax=docker/dockerfile:1
FROM maven:3.9.12-eclipse-temurin-21-alpine AS builder

WORKDIR /build
COPY examples/java/pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline
COPY examples/java/src ./src
RUN mvn -q package

FROM eclipse-temurin:21-jre-alpine AS runtime

ENV PORT=62900
WORKDIR /app
COPY --from=builder /build/target/marona-mcp-java-0.1.0.jar ./server.jar
RUN addgroup -S -g 10001 marona \
    && adduser -S -D -H -u 10001 -G marona marona \
    && chown -R marona:marona /app

USER 10001:10001
EXPOSE 62900
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --quiet --spider http://127.0.0.1:62900/health || exit 1
ENTRYPOINT ["java", "-jar", "/app/server.jar"]

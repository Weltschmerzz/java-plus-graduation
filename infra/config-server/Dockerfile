FROM eclipse-temurin:21-jre

WORKDIR /app

COPY infra/config-server/target/config-server-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

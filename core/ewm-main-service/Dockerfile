FROM eclipse-temurin:21-jre

WORKDIR /app

COPY core/ewm-main-service/main-server/target/main-server-*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

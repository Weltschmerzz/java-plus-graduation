FROM eclipse-temurin:21-jre
WORKDIR /app

COPY core/additional-service/target/additional-service-0.0.1-SNAPSHOT-exec.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

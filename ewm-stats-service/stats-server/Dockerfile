FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .
RUN mvn -pl ewm-stats-service/stats-server -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/ewm-stats-service/stats-server/target/*.jar app.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]

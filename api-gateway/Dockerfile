FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /workspace
COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/
COPY api-gateway/pom.xml api-gateway/
COPY auth-service/pom.xml auth-service/

COPY api-gateway api-gateway
RUN mvn clean package -pl api-gateway -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

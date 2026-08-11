FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /workspace
COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/
COPY api-gateway/pom.xml api-gateway/
COPY auth-service/pom.xml auth-service/
COPY patient-service/pom.xml patient-service/
COPY doctor-service/pom.xml doctor-service/
COPY appointment-service/pom.xml appointment-service/
COPY notification-service/pom.xml notification-service/

COPY appointment-service appointment-service
RUN mvn clean package -pl appointment-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/appointment-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]

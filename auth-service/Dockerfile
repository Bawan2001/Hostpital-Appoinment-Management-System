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

COPY auth-service auth-service
RUN mvn clean package -pl auth-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/auth-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

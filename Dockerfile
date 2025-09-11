FROM openjdk
WORKDIR /app
COPY target/repeatit-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

#FROM maven:3.8.7-openjdk-18-slim AS builder
#WORKDIR /app
#VOLUME /root/.m2
#COPY pom.xml .
#COPY src ./src
#RUN mvn clean package -DskipTests
#
#
#FROM openjdk
#WORKDIR /app
#COPY --from=builder /app/target/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]
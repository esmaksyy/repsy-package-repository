FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /build
COPY . . 
RUN mvn clean package -DskipTests

FROM openjdk:17
WORKDIR /app
COPY --from=build /build/app-main/target/app-main-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
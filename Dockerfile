# Build stage
FROM amazoncorretto:21 AS build
COPY . /src
WORKDIR /src
RUN ./gradlew build --no-daemon -x test

# Run stage
FROM amazoncorretto:21-alpine
EXPOSE 8080
COPY --from=build /src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

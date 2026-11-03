# ---- Stage 1: build + test (the image build fails if a test fails) ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
COPY data data
RUN mvn -B -q package

# ---- Stage 2: small runtime image ----
FROM eclipse-temurin:21-jre
WORKDIR /app
ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8 \
    FOODFLOW_DATA=/app/data/restaurants.json
COPY --from=build /app/target/foodflow.jar app.jar
COPY data data
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]

FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q compile dependency:copy-dependencies -DoutputDirectory=target/lib


FROM eclipse-temurin:24-jdk
WORKDIR /app
COPY --from=build /app/target/classes ./classes
COPY --from=build /app/target/lib ./lib
CMD ["java", "-cp", "classes:lib/*", "com.nasim.ai.nutrion.health.HealthAssistantMain"]
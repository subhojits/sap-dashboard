# -------- Build Stage --------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY . .
RUN ./mvnw clean package -DskipTests

# -------- Run Stage --------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Copy Kafka truststore
COPY ssl/kafka-truststore.jks /app/kafka-truststore.jks


EXPOSE 8080

ENV PORT=8080

ENTRYPOINT ["java", "-jar", "app.jar"]

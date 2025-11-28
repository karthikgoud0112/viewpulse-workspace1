FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the pre-built JAR produced in target/
COPY target/*.jar viewpulse-backend.jar

RUN mkdir -p /app/uploads/videos
EXPOSE 8080
ENTRYPOINT ["java","-jar","viewpulse-backend.jar"]

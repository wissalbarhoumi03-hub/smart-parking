FROM eclipse-temurin:17-jre

ARG jarToCopy

COPY target/$jarToCopy /app/app.jar

CMD ["java", "-cp", "/app/app.jar", "com.example.smart_parking.App"]
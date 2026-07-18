FROM bellsoft/liberica-openjre-alpine:21

RUN addgroup -S app && adduser -S app -G app
USER app
WORKDIR /app

COPY build/libs/sample-service-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

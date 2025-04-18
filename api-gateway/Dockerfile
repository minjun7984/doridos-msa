FROM openjdk:17

WORKDIR /app

COPY ./build/libs/*SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

CMD ["java", "-jar", "app.jar"]
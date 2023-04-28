FROM openjdk:17-jdk-slim-buster
WORKDIR /app
COPY ./target/app-server-0.0.1.jar /app/myapp.jar
COPY ./src/main/resources/service_account_key.json /app/resources
ENV FILE_PATH /app/resources/service_account_key.json
CMD ["java", "-Dextenal.file=${FILE_PATH}", "-jar", "myapp.jar"]
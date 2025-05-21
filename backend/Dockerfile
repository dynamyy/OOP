FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

RUN apt-get update
RUN apt-get install -y wget
RUN wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN apt-get install -y ./google-chrome-stable_current_amd64.deb

WORKDIR /app

COPY selver.txt .

COPY --from=build /app/target/OOP_projekt-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "OOP_projekt-0.0.1-SNAPSHOT.jar"]
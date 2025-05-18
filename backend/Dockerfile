FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y \
    wget \
    gnupg2 \
    curl \
    libx11-dev \
    libx264-dev \
    libx265-dev \
    libvpx-dev \
    libnss3 \
    libatk-bridge2.0-0 \
    libatk1.0-0 \
    libcups2 \
    libxss1 \
    libgdk-pixbuf2.0-0 \
    libpangocairo-1.0-0 \
    libdbus-1-3 \
    libgdk-pixbuf2.0-0 \
    libnspr4 \
    libnss3 \
    libx11-xcb1 \
    libxrandr2 \
    libgbm-dev \
    libasound2 \
    fonts-liberation \
    libappindicator3-1 \
    libxtst6 \
    libxcomposite1 \
    libxdamage1 \
    libnss3 \
    xdg-utils \
    && apt-get clean

RUN apt-get update
RUN apt-get install -y wget
RUN wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN apt-get install -y ./google-chrome-stable_current_amd64.deb

WORKDIR /app

COPY --from=build /app/target/OOP_projekt-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "OOP_projekt-0.0.1-SNAPSHOT.jar"]
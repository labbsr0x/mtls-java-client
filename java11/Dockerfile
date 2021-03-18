FROM openjdk:11-slim-buster

COPY src /usr/src/app
WORKDIR /usr/src/app
RUN javac -cp . App.java
CMD ["java", "App"]
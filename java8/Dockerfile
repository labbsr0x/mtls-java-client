FROM openjdk:8-slim-buster

COPY src /usr/src/app
WORKDIR /usr/src/app
RUN javac -cp . App.java

CMD ["/bin/sh","-c","java App --url=$TARGET_ENDPOINT"]
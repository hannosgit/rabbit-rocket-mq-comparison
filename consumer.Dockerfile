FROM openjdk:11

ARG APPLICATION_NAME=first-prototype-consumer-0.0.1-SNAPSHOT.jar
ENV APPLICATION_NAME=$APPLICATION_NAME

WORKDIR /project

RUN mkdir /project/results

COPY ./Consumer/target/${APPLICATION_NAME} /project/${APPLICATION_NAME}

#run the spring boot application
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -jar /project/${APPLICATION_NAME}
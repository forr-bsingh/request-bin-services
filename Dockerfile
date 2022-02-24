# Setting up build env
FROM --platform=linux/amd64 maven:3.6.3-openjdk-11-slim as builder
LABEL MAINTAINER="Baljeet Singh {sharpedge2005@gmail.com}"

COPY . /tmp/

WORKDIR /tmp

RUN mvn -DskipTests=true package spring-boot:repackage

# Pull base image.
FROM openjdk:11.0.10-jre-slim
LABEL MAINTAINER="Baljeet Singh {sharpedge2005@gmail.com}"
LABEL DESC="Service layer for REQUEST-BIN"

#RUN apt update && apt upgrade -y
#RUN apt autoremove -y
#RUN apt install curl -y

#Setting build args passed from build.
ARG SERVICE_NAME=request-bin-services
ARG SERVICE_PORT=9090
ARG GRPC_PORT=9089

#Building env variable for working directory and volume
ENV service.port ${SERVICE_PORT}
ENV grpc.port ${GRPC_PORT}
ENV app.home /opt/${SERVICE_NAME}
ENV profile production

WORKDIR /opt/${SERVICE_NAME}
COPY --from=builder /tmp/target/${SERVICE_NAME}.jar services.jar

EXPOSE ${SERVICE_PORT}
EXPOSE ${GRPC_PORT}

ENTRYPOINT ["java"]
CMD ["-jar", "services.jar"]
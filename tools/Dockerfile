FROM openjdk:11
ARG service_port
ADD *.jar /opt/boot.jar
WORKDIR /opt
ENTRYPOINT ["java", "-jar", "boot.jar"]
EXPOSE $service_port


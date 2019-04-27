#!/bin/bash

BOOTJAR_PROJECT=${1:-.}
BOOTAPP_SERVICE_PORT=${2:-8080}

BOOTJAR_FILE=$(find ${BOOTJAR_PROJECT} -name '*-boot.jar' | head -n1)
[[ -z BOOTJAR_FILE ]] && echo '*-boot.jar not found' && exit 1

echo $BOOTJAR_FILE

BOOTJAR_FULLNAME=$(basename $BOOTJAR_FILE)
BOOTJAR_BASENAME=${BOOTJAR_FULLNAME%-boot.jar}
IMAGE_TAG_NAME=${BOOTJAR_BASENAME%-*}
IMAGE_TAG_VERSION=${BOOTJAR_BASENAME##*-}
echo

BOOTJAR_TMP="$(mktemp -d)"
cp $BOOTJAR_FILE $BOOTJAR_TMP

cd $BOOTJAR_TMP
cat <<'EOT' > Dockerfile
FROM openjdk:11
ARG service_port
ADD *.jar /opt/boot.jar
WORKDIR /opt
ENTRYPOINT ["java", "-jar", "boot.jar"]
EXPOSE $service_port
EOT

echo $BOOTJAR_TMP
ls $BOOTJAR_TMP

docker build -t "$IMAGE_TAG_NAME:$IMAGE_TAG_VERSION" --build-arg service_port=$BOOTAPP_SERVICE_PORT .

#!/bin/bash

./mvnw -Drevision=local package -DskipTests

JAR_NAME=$(./mvnw -Drevision=local help:evaluate -Dexpression=project.build.finalName -q -DforceStdout)-shaded.jar

IMAGE_NAME=native-application

docker build --build-arg PROFILE=iptvtest5 --build-arg JAR="$JAR_NAME" -t $IMAGE_NAME .

echo "Done, built image -- $IMAGE_NAME"
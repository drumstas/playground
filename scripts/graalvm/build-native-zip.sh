#!/bin/bash

while getopts p:j: opt; do
  case $opt in
  p) profile=$OPTARG ;;
  j) jar_name=$OPTARG ;;
  *)
    echo 'Error in command line parsing' >&2
    exit 1
    ;;
  esac
done

: "${profile:?Missing profile property, provide it with the -p flag}"
: "${jar_name:?Missing jar name property, provide it with the -j flag}"

START_TIME=$(date +%s)

IMAGE_NAME=native-application-builder
docker build --target builder --build-arg PROFILE="$profile" --build-arg JAR="$jar_name" -t $IMAGE_NAME .

CONTAINER_ID=$(docker create $IMAGE_NAME)
docker cp "$CONTAINER_ID":custom-runtime/lambda.zip target/
docker rm "$CONTAINER_ID" >/dev/null

END_TIME=$(date +%s)

echo "Done, it took $((END_TIME - START_TIME)) seconds to prepare the native image zip."

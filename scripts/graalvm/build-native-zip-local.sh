#!/bin/bash

./mvnw -Drevision=local package -DskipTests

JAR=$(./mvnw -Drevision=local help:evaluate -Dexpression=project.build.finalName -q -DforceStdout)-shaded.jar

./build-native-zip.sh -p iptvtest5 -j "$JAR"

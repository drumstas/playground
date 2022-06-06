FROM ghcr.io/graalvm/native-image:ol8-java17-22.0.0.2 as builder

# Copy files needed for native image generation
ARG JAR
COPY target/$JAR /
COPY /graal /graal

ENV REFLECT_CFG=/graal/reflect

# Generate the image
RUN native-image \
    -H:+StaticExecutableWithDynamicLibC \
    -H:DynamicProxyConfigurationFiles=/graal/proxy-config.json \
    -H:ReflectionConfigurationFiles=$REFLECT_CFG/lambda-events.json \
    -jar /$JAR \
    --enable-http \
    -J-Dfile.encoding=UTF-8 \
    native-application


FROM alpine:3.15 as runner

RUN apk add gcompat

ARG PROFILE
ENV ENV_PROFILE=$PROFILE

COPY --from=builder /native-application /

ENTRYPOINT ./native-application -Dspring.profiles.active=$ENV_PROFILE

CMD org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest
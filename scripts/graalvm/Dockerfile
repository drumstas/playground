FROM ghcr.io/graalvm/native-image:ol8-java17-22.0.0.2 as builder

RUN microdnf install zip

ARG JAR
ARG PROFILE

COPY target/$JAR /
COPY graal /graal

ENV REFLECT_CFG=/graal/reflect
WORKDIR /custom-runtime

RUN native-image \
    -H:DynamicProxyConfigurationFiles=/graal/proxy-config.json \
    -H:ReflectionConfigurationFiles=$REFLECT_CFG/metadata-model.json,$REFLECT_CFG/lambda-events.json \
    -jar /$JAR \
    --enable-http \
    -J-Dfile.encoding=UTF-8 \
    native-application

# Setup for AWS custom runtime (the bootstrap file acts as an entrypoint for launching AWS lambda)
RUN echo "#!/bin/sh" >> bootstrap \
    && echo "cd "\${LAMBDA_TASK_ROOT:-.}" || exit" >> bootstrap \
    && echo "./native-application -Dspring.profiles.active=$PROFILE" >> bootstrap

RUN zip lambda.zip *


FROM alpine:3.15 as runner

RUN apk add gcompat

COPY --from=builder /custom-runtime/native-application /

ARG PROFILE
ENV ENV_PROFILE=$PROFILE

ENTRYPOINT ./native-application -Dspring.profiles.active=$ENV_PROFILE

CMD org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest

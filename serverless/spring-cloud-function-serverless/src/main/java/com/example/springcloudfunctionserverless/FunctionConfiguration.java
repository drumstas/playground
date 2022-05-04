package com.example.springcloudfunctionserverless;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class FunctionConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FunctionConfiguration.class);

    @Bean
    public Function<String, String> uppercase() {
        return value -> {
            log.info("Processing uppercase for string '{}'", value);
            return value.toUpperCase();
        };
    }

    @Bean
    public Supplier<String> randomString() {
        return () -> UUID.randomUUID().toString();
    }

    @Bean
    public Consumer<S3Event> processS3Event() {
        return s3Event -> {
            var bucket = s3Event.getRecords().get(0).getS3().getBucket().getName();
            var key = s3Event.getRecords().get(0).getS3().getObject().getKey();

            log.info("Something was uploaded to S3: {}/{}", bucket, key);
        };
    }

    @Bean
    public Function<Message<Person>, Message<Person>> processPerson() {
        return message -> {
            var person = message.getPayload();
            log.info("Processing incoming person '{}'", person);

            person.setId(UUID.randomUUID().toString());
            log.info("Successfully stored person in database with id '{}'", person.getId());

            Map<String, Object> resultHeaders = Map.of(
                    "statuscode", HttpStatus.CREATED.value(),
                    "X-Custom-Header", "Hello World from Spring Cloud Function AWS Adapter"
            );

            return new GenericMessage<>(person, resultHeaders);
        };
    }

    @Bean
    public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> processXmlOrder() {
        return value -> {
            try {
                var objectMapper = new XmlMapper();
                var order = objectMapper.readValue(value.getBody(), Order.class);

                order.setProcessed(true);

                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(201)
                        .withHeaders(Map.of("Content-Type", "application/xml"))
                        .withBody(objectMapper.writeValueAsString(order));
            } catch (IOException e) {
                e.printStackTrace();
                return new APIGatewayProxyResponseEvent().withStatusCode(500);
            }
        };
    }

}

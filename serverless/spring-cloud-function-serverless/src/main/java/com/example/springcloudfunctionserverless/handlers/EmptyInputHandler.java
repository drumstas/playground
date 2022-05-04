package com.example.springcloudfunctionserverless.handlers;

import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

public class EmptyInputHandler extends SpringBootRequestHandler<Void, String> {
}

package com.example.bootnative

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router
import org.springframework.web.util.UriBuilder
import java.security.MessageDigest

@Configuration
class MarvelConfig {

    @Bean
    fun webClient(props: MarvelProperties) = WebClient.create("${props.serverUrl}/v1/public")

    @Bean
    fun md5(): MessageDigest = MessageDigest.getInstance("MD5")

    @Bean
    fun routes(client: WebClient, props: MarvelProperties, digest: MessageDigest) = router {
        GET("/") { request ->
            val mono = client
                .get()
                .uri {
                    it.path("/characters")
                        .queryParamsWith(request)
                        .build()
                }.retrieve()
                .bodyToMono<String>()
            ServerResponse.ok().body(mono)
        }
    }
}

private fun UriBuilder.queryParamsWith(request: ServerRequest) = apply {
    arrayOf("limit", "offset", "orderBy").forEach { param ->
        request.queryParam(param).ifPresent {
            queryParam(param, it)
        }
    }
}
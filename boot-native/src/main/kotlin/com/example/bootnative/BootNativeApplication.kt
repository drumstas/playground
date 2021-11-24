package com.example.bootnative

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class BootNativeApplication

fun main(args: Array<String>) {
	runApplication<BootNativeApplication>(*args)
}

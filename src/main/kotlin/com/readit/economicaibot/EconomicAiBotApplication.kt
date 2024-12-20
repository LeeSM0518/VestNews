package com.readit.economicaibot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class EconomicAiBotApplication

fun main(args: Array<String>) {
    runApplication<EconomicAiBotApplication>(*args)
}

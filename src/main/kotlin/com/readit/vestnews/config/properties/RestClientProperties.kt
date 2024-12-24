package com.readit.vestnews.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "rest-client")
data class RestClientProperties(
    val connectTimeout: Long,
    val readTimeout: Long,
)

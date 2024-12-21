package com.readit.economicaibot.config

import com.readit.economicaibot.config.properties.RestClientProperties
import java.time.Duration
import org.springframework.boot.web.client.ClientHttpRequestFactories
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings
import org.springframework.boot.web.client.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.web.client.RestClient

@Configuration
class OpenAiConfiguration(
    private val properties: RestClientProperties,
) {

    @Bean
    fun restClientCustomizer(): RestClientCustomizer =
        RestClientCustomizer { restClientBuilder: RestClient.Builder ->
            restClientBuilder
                .requestFactory(
                    BufferingClientHttpRequestFactory(
                        ClientHttpRequestFactories.get(
                            ClientHttpRequestFactorySettings.DEFAULTS
                                .withConnectTimeout(Duration.ofSeconds(properties.connectTimeout))
                                .withReadTimeout(Duration.ofSeconds(properties.readTimeout))
                        )
                    )
                )
        }
}

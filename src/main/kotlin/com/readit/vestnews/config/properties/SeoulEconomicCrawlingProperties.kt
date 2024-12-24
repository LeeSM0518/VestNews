package com.readit.vestnews.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "crawling.seoul-economic")
data class SeoulEconomicCrawlingProperties(
    val financeUrl: String,
    val internationalUrl: String,
    val certificateUrl: String,
)

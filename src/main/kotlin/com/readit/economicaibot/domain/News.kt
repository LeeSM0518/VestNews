package com.readit.economicaibot.domain

import java.time.Instant

data class News(
    val title: String,
    val content: String,
    val link: String,
    val date: Instant,
)

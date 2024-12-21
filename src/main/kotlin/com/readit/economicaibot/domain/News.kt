package com.readit.economicaibot.domain

import com.readit.economicaibot.config.serializer.InstantSerializer
import java.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class News(
    val title: String,
    val content: String,
    val link: String,
    @Serializable(with = InstantSerializer::class)
    val date: Instant,
)

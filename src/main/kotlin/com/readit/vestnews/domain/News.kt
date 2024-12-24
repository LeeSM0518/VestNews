package com.readit.vestnews.domain

import com.readit.vestnews.config.serializer.InstantSerializer
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

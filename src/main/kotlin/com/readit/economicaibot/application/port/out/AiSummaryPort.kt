package com.readit.economicaibot.application.port.out

interface AiSummaryPort {
    suspend fun summarize(content: String): String
}

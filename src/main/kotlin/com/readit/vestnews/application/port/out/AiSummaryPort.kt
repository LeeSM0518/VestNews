package com.readit.vestnews.application.port.out

interface AiSummaryPort {
    suspend fun summarize(content: String): String
}

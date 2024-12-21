package com.readit.economicaibot.application.port.out

interface AiPortfolioPort {
    suspend fun construct(content: String): String
}

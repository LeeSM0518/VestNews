package com.readit.vestnews.application.port.out

interface AiPortfolioPort {
    suspend fun construct(content: String): String
}

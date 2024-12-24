package com.readit.vestnews.application.service

import com.readit.vestnews.application.port.out.AiPortfolioPort
import org.springframework.stereotype.Service

@Service
class InvestmentPortfolioService(
    private val aiPortfolioPort: AiPortfolioPort
) {


    suspend fun construct(content: String): String {
        return aiPortfolioPort.construct(content)
    }
}

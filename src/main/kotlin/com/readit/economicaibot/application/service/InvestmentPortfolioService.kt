package com.readit.economicaibot.application.service

import com.readit.economicaibot.application.port.out.AiPortfolioPort
import org.springframework.stereotype.Service

@Service
class InvestmentPortfolioService(
    private val aiPortfolioPort: AiPortfolioPort
) {


    suspend fun construct(content: String): String {
        return aiPortfolioPort.construct(content)
    }
}

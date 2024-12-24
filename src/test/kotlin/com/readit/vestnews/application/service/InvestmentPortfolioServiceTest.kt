package com.readit.vestnews.application.service

import com.readit.vestnews.config.IntegrationTest
import kotlin.test.Test
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource

@IntegrationTest
internal class InvestmentPortfolioServiceTest @Autowired constructor(
    private val investmentPortfolioService: InvestmentPortfolioService,
) {
    @Test
    fun `요약된 글을 통해 투자 포트폴리오를 생성할 수 있다`() = runBlocking {
        val content = ClassPathResource("20241222-4o-summary.txt").file.readText()
        val portfolio = investmentPortfolioService.construct(content)
        println("\n\n=======================\n\n")
        println(portfolio)
    }
}

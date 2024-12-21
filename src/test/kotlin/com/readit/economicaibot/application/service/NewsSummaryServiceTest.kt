package com.readit.economicaibot.application.service

import com.readit.economicaibot.config.IntegrationTest
import com.readit.economicaibot.domain.News
import kotlin.test.Test
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource

@IntegrationTest
internal class NewsSummaryServiceTest @Autowired constructor(
    private val service: NewsSummaryService,
) {

    @Test
    fun `경제 기사를 요약할 수 있다`() = runBlocking {
        val result = ClassPathResource("20241101-60-news.json").file.readText()
        val news: List<News> = Json.decodeFromString(result)
        val summary = service.summarize(news)
        println(summary)
    }
}

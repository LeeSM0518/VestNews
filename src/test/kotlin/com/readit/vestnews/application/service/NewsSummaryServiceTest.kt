package com.readit.vestnews.application.service

import com.readit.vestnews.config.IntegrationTest
import com.readit.vestnews.domain.News
import kotlin.test.Test
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource

@IntegrationTest
internal class NewsSummaryServiceTest @Autowired constructor(
    private val service: NewsSummaryService,
) {

    @Test
    fun `경제 기사를 요약할 수 있다`() = runBlocking {
        val result = ClassPathResource("20241222-60-news.json").file.readText()
        val news: List<News> = Json.decodeFromString(result)
        val summary = service.summarize(news)
        println(summary)
    }
}

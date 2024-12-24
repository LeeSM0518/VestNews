package com.readit.vestnews.adapter.out

import com.readit.vestnews.config.IntegrationTest
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
internal class SeoulEconomicCrawlerTest @Autowired constructor(
    private val crawler: SeoulEconomicCrawler,
) {

    @Test
    fun `서울 경제의 금융 기사를 크롤링 할 수 있다`() = runTest {
        val localDateTime = LocalDate.of(2024, 12, 22).atStartOfDay()
        val zoneId = ZoneId.of("Asia/Seoul")
        val zonedDateTime = localDateTime.atZone(zoneId)
        val results = crawler.crawling(zonedDateTime.toInstant())

        assertThat(results).isNotEmpty
        val file = File("20241222-60-news.json")
        file.createNewFile()
        Json {
            prettyPrint = true
        }.encodeToString(results).also { file.writeText(it) }
    }
}

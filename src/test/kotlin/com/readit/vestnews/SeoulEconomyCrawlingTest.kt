package com.readit.vestnews

import com.readit.vestnews.config.IntegrationTest
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@IntegrationTest
internal class SeoulEconomyCrawlingTest @Autowired constructor(
    private val webTestClient: WebTestClient,
) {

    data class NewsItem(
        val title: String?,
        val link: String?,
        val date: String?,
    )

    @Test
    fun `서울 경제 기사를 크롤링 할 수 있다`() = runTest {
        val page = 1
        val date = "20241219"
        val items = getNewsItems(date, page)

        items.forEach {
            assertThat(it.title).isNotNull()
            assertThat(it.link).isNotNull()
            assertThat(it.date).isNotNull()
        }
    }

    @Test
    fun `서울 경제 기사를 특정 날짜에 대한 모든 기사를 크롤링 할 수 있다`() = runTest {
        val allItems = mutableListOf<NewsItem>()
        var page = 1

        while (true) {
            val newsItems = getNewsItems("20241219", page)
            allItems.addAll(newsItems)
            if (newsItems.isEmpty()) break
            page++
        }

        allItems.forEach {
            assertThat(it.title).isNotNull()
            assertThat(it.link).isNotNull()
            assertThat(it.date).isNotNull()
        }
    }

    @Test
    fun `서울 경제 기사의 상세 내용을 크롤링 할 수 있다`() = runTest {
        val response = webTestClient
            .get()
            .uri("https://m.sedaily.com/NewsView/2DI6LCU2TE")
            .exchange()
            .expectBody<String>()
            .returnResult()
            .responseBody!!

        val content = Jsoup.parse(response).selectFirst("div.article")
        assertThat(content).isNotNull
    }

    private fun getNewsItems(date: String, page: Int): List<NewsItem> {
        val result = webTestClient
            .get()
            .uri("https://m.sedaily.com/RankAll/GA/$date/$page")
            .exchange()
            .expectBody<String>()
            .returnResult()
            .responseBody!!

        val document = Jsoup.parse(result)

        val items = document
            .select("ul#newsList > li")
            .map {
                val title = it.selectFirst("div.report_tit > a")
                val date = it.selectFirst("span.time")?.text()?.trim()
                NewsItem(
                    title = title?.text()?.trim(),
                    link = title?.attr("href")?.trim(),
                    date = date
                )
            }
        return items
    }

}

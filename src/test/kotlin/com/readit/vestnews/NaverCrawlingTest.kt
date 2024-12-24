package com.readit.vestnews

import com.fasterxml.jackson.annotation.JsonProperty
import com.readit.vestnews.config.IntegrationTest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@IntegrationTest
internal class NaverCrawlingTest @Autowired constructor(
    private val webTestClient: WebTestClient,
) {

    data class NewsResponse(
        val component: Map<String, String> = emptyMap(),
        val renderedComponent: RenderedComponent,
        val uhv: String = "",
    )


    data class RenderedComponent(
        @JsonProperty("SECTION_ARTICLE_LIST_FOR_LATEST")
        val articles: String,
    )

    data class NewsItem(
        val title: String,
        val link: String,
        val date: String,
    )

    data class NewsDetailItem(
        val title: String?,
        val content: String?,
        val date: String?,
    ) {
        constructor(
            document: Document,
        ) : this(
            title = document.selectFirst("meta[property=og:title]")?.attr("content")?.trim(),
            content = document.selectFirst("article")?.text()?.trim(),
            date = document.selectFirst("span._ARTICLE_DATE_TIME")?.attr("data-date-time")?.trim(),
        )
    }

    fun extractNewsItems(html: String): List<NewsItem> {
        val document: Document = Jsoup.parse(html)
        val elements: Elements = document.select("div.sa_text")
        return elements.map { element ->
            val title = element.selectFirst("strong.sa_text_strong")!!.text()
            val content = element.select("a").attr("href")
            val date = element.select("div.sa_text_datetime").text()
            NewsItem(title, content, date)
        }
    }


    @Test
    fun `네이버 경제 기사를 크롤링 할 수 있다`() = runTest {
        val newsItems = getNewsItem()

        assertThat(newsItems).isNotEmpty
    }

    @Test
    fun `네이버 경제 기사의 상세 내용을 크롤링 할 수 있다`() = runTest {
        val response = webTestClient
            .get()
            .uri("https://n.news.naver.com/mnews/article/366/0001041291")
            .exchange()
            .expectBody<String>()
            .returnResult()
            .responseBody!!

        val document = Jsoup.parse(response)

        val title = document.selectFirst("meta[property=og:title]")?.attr("content")?.trim()
        val content = document.selectFirst("article")?.text()?.trim()
        val publicationDate = document.selectFirst("span._ARTICLE_DATE_TIME")?.attr("data-date-time")?.trim()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        LocalDateTime.parse(publicationDate, formatter)

        assertThat(title).isNotNull()
        assertThat(content).isNotNull()
        assertThat(publicationDate).isNotNull()
    }

    @Test
    fun `하루 동안의 뉴스를 조회할 수 있다`() = runTest {
        val newsList = mutableListOf<NewsItem>()
        var page = 1

        while (true) {
            val newsItems = getNewsItem(page)
            newsList.addAll(newsItems)
            if (newsItems.find { it.date == "1일전" } != null) {
                break
            }
            page++
            println(newsItems)
            println(newsList.size)
        }

        println(newsList.size)
    }

    @Test
    fun `네이버 경제 기사 목록과 상세 내용을 모두 크롤링 할 수 있다`() = runTest {
        val newItems = getNewsItem()

        val newsDetailItems = newItems
            .asFlow()
            .map {
                async(Dispatchers.IO) {
                    webTestClient
                        .get()
                        .uri(it.link)
                        .exchange()
                        .expectBody<String>()
                        .returnResult()
                        .responseBody!!
                }
            }
            .buffer()
            .map { NewsDetailItem(Jsoup.parse(it.await())) }
            .toList()

        newsDetailItems.forEach {
            assertThat(it.title).isNotNull()
            assertThat(it.content).isNotNull()
            assertThat(it.date).isNotNull()
        }
    }

    private fun getNewsItem(page: Int = 1): List<NewsItem> {
        val response = webTestClient
            .get()
            .uri("https://news.naver.com/section/template/SECTION_ARTICLE_LIST_FOR_LATEST?sid=101&sid2=258&pageNo=$page")
            .exchange()
            .expectBody<NewsResponse>()
            .returnResult()
            .responseBody!!

        val newsItems = extractNewsItems(response.renderedComponent.articles)
        return newsItems
    }
}

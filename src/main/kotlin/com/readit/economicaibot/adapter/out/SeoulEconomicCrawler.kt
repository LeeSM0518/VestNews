package com.readit.economicaibot.adapter.out

import com.readit.economicaibot.adapter.out.client.CrawlingClient
import com.readit.economicaibot.application.port.out.NewsCrawlingPort
import com.readit.economicaibot.config.properties.SeoulEconomicCrawlingProperties
import com.readit.economicaibot.domain.News
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.transform
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component


@Component
class SeoulEconomicCrawler(
    properties: SeoulEconomicCrawlingProperties,
) : NewsCrawlingPort {

    private val financeClient = CrawlingClient(properties.financeUrl)
    private val internationalClient = CrawlingClient(properties.internationalUrl)
    private val certificateClient = CrawlingClient(properties.certificateUrl)

    private val timezone = ZoneId.of("Asia/Seoul")
    private val instantFormatter = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(timezone)
    private val stringFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm").withZone(timezone)

    override suspend fun crawling(date: Instant): List<News> = coroutineScope {
        val formattedDate = instantFormatter.format(date)
        listOf(
            async { crawling(financeClient, formattedDate).toList() },
            async { crawling(internationalClient, formattedDate).toList() },
            async { crawling(certificateClient, formattedDate).toList() }
        ).awaitAll().flatten()
    }

    private fun CoroutineScope.crawling(
        crawlingClient: CrawlingClient,
        formattedDate: String?,
    ): Flow<News> =
        (1..2)
            .asFlow()
            .map { async(Dispatchers.IO) { extractSimpleNews(crawlingClient.getDocument("$formattedDate/$it")) } }
            .buffer()
            .transform { job -> job.await().forEach { news -> emit(news) } }
            .map { (title, date, link) ->
                async(Dispatchers.IO) { fetchArticle(title, date, link) }
            }
            .buffer()
            .map { it.await() }

    private fun fetchArticle(title: String, date: String, link: String): News {
        val content = CrawlingClient(link)
            .getDocument()
            .selectFirst("div.article")
            ?.text()
            ?.trim()
            ?: throw NoSuchElementException("기사 내용이 존재하지 않습니다")
        return News(
            title = title,
            content = content,
            link = link,
            date = LocalDateTime.parse(date, stringFormatter).atZone(timezone).toInstant()
        )
    }

    private fun extractSimpleNews(document: Document) =
        document
            .select("ul#newsList > li")
            .map {
                val element = it.selectFirst("div.report_tit > a")
                val title = element?.text()?.trim()
                    ?: throw NoSuchElementException("기사 제목이 존재하지 않습니다.")
                val date = it.selectFirst("span.time")?.text()?.trim()
                    ?: throw NoSuchElementException("기사 날짜가 존재하지 않습니다.")
                val link = element.attr("href").trim()
                Triple(title, date, link)
            }

}

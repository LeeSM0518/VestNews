package com.readit.vestnews.application.service

import com.readit.vestnews.application.port.out.AiSummaryPort
import com.readit.vestnews.domain.News
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class NewsSummaryService(
    private val aiSummaryPort: AiSummaryPort,
) {

    private val logger = LoggerFactory.getLogger(NewsSummaryService::class.java)
    private var count = 0

    suspend fun summarize(newsList: List<News>): String = coroutineScope {
        val result = newsList
            .map { it.content }
            .reduceSummary()

        count = 0
        aiSummaryPort.summarize(result)
    }

    private suspend fun List<String>.reduce(size: Int) =
        withContext(Dispatchers.IO) {
                chunked(size)
                .asFlow()
                .map { asyncReduceSummary(it) }
                .buffer()
                .map { it.await() }
                .toList()
        }

    private fun CoroutineScope.asyncReduceSummary(it: List<String>): Deferred<String> =
        async(Dispatchers.IO) { it.reduceSummary() }

    private suspend fun List<String>.reduceSummary(): String =
        reduce { accumulator, news ->
            count++
            logger.info("기사 요약 횟수 : $count")
            val text = accumulator.addNews(news)
            if (text.length >= MAX_TEXT_COUNT) {
                val summarized = aiSummaryPort.summarize(accumulator)
                "# 기사들을 요약한 내용 \n\n $summarized".addNews(news)
            }
            else text
        }

    fun String.addNews(news: String) =
        "$this \n --- \n $news \n"

    companion object {
        const val MAX_TEXT_COUNT = 4_000
    }
}

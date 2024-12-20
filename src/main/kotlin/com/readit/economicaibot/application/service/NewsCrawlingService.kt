package com.readit.economicaibot.application.service

import com.readit.economicaibot.application.port.out.NewsCrawlingPort
import com.readit.economicaibot.domain.News
import java.time.Instant
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class NewsCrawlingService(
    private val newsCrawlingPort: NewsCrawlingPort,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun getNewsCrawlingByDate(date: Instant): List<News> =
        runCatching {
            newsCrawlingPort.crawling(date).toList()
        }.getOrElse {
            logger.error("크롤링을 실패했습니다.", it)
            throw IllegalAccessException("크롤링을 실패했습니다.")
        }
}

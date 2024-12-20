package com.readit.economicaibot.application.port.out

import com.readit.economicaibot.domain.News
import java.time.Instant

interface NewsCrawlingPort {

    suspend fun crawling(date: Instant): List<News>

}

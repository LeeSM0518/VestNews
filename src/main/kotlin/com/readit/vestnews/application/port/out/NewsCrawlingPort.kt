package com.readit.vestnews.application.port.out

import com.readit.vestnews.domain.News
import java.time.Instant

interface NewsCrawlingPort {

    suspend fun crawling(date: Instant): List<News>

}

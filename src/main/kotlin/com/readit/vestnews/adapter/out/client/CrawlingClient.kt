package com.readit.vestnews.adapter.out.client

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class CrawlingClient(
    private val mainUrl: String,
) {
    fun getDocument(subUrl: String = ""): Document = Jsoup.connect("$mainUrl/$subUrl").get()
}

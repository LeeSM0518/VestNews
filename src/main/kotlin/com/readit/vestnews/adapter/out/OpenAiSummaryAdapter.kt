package com.readit.vestnews.adapter.out

import com.readit.vestnews.application.port.out.AiSummaryPort
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

@Component
class OpenAiSummaryAdapter(
    builder: ChatClient.Builder,
) : AiSummaryPort {

    private val chatClient: ChatClient =
        builder
            .defaultSystem(ClassPathResource("open-ai-summary-prompt.txt"))
            .defaultAdvisors(SimpleLoggerAdvisor())
//            .defaultOptions(
//                ChatOptions
//                    .builder()
//                    .model("gpt-3.5-turbo")
//                    .build()
//            )
            .build()

    override suspend fun summarize(content: String): String =
        chatClient
            .prompt()
            .user(content)
            .call()
            .content()
            ?: ""
}

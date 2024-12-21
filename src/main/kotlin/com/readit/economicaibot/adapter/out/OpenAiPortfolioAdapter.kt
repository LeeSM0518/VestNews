package com.readit.economicaibot.adapter.out

import com.readit.economicaibot.application.port.out.AiPortfolioPort
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

@Component
class OpenAiPortfolioAdapter(
    builder: ChatClient.Builder,
): AiPortfolioPort {

    private val chatClient: ChatClient =
        builder
//            .defaultSystem(ClassPathResource("open-ai-portfolio-prompt.txt"))
            .defaultAdvisors(SimpleLoggerAdvisor())
            .defaultOptions(
                ChatOptions
                    .builder()
                    .model("o1-preview")
                    .temperature(1.0)
                    .build()
//                ChatOptions
//                    .builder()
//                    .temperature(1.0)
//                    .build()
            )
            .build()

    override suspend fun construct(content: String): String =
        chatClient
            .prompt(ClassPathResource("open-ai-portfolio-prompt.txt").file.readText())
//            .prompt()
            .user(content)
            .call()
            .content()
            ?: ""
}

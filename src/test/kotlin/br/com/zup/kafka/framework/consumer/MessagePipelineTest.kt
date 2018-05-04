package br.com.zup.kafka.framework.consumer

import org.junit.jupiter.api.Test
import java.time.Duration

class MessagePipelineTest {

    @Test
    fun shouldCreateMessagePipeline() {
        val pipeline = MessagePipeline("customer")
            .withListener((m: Message) { })
            .withConcurrentListeners(3)
            .withRetrials(3, Duration.ofMinutes(1))
            .withRetryListener((m: Message) { })
            .withDLQListener()
            .build()

        pipeline.start()
    }

}
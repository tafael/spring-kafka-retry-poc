package br.com.zup.kafka.app.consumer

import io.zup.springframework.kafka.annotation.RetryKafkaListener
import io.zup.springframework.kafka.annotation.RetryPolicy
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.lang.RuntimeException

@Component
open class MainConsumer {

    @RetryPolicy(id = "main_topic_id", topic = "retry_topic", retries = 3, dlqTopic = "dlq_topic")
    @KafkaListener(topics = ["main_topic"])
    open fun listen(message: String) {
        println("Consuming message")
        throw RuntimeException("Bad things")
    }

    @RetryKafkaListener(retryPolicyId = "main_topic_id")
    @KafkaListener(topics = ["retry_topic"])
    open fun retry(message: String) {
        println("Retrying message")
        throw RuntimeException("Bad things retried")
    }

    @KafkaListener(topics = ["dlq_topic"])
    open fun listenDLQ(message: String) {
        println("Receiveid in dlq topic ${message}")
    }

}

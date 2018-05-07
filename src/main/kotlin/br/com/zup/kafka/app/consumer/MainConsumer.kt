package br.com.zup.kafka.app.consumer

import br.com.zup.kafka.framework.annotation.RetryKafkaListener
import br.com.zup.kafka.framework.annotation.RetryPolicy
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.lang.RuntimeException

@Component
open class MainConsumer {

    @RetryPolicy(topic = "retry_topic", retries = 3, dlqTopic = "dlq_topic")
    @KafkaListener(topics = ["main_topic"])
    open fun listen(message: String) {
        println("Consuming message")
        throw RuntimeException("Bad things")
    }

    @RetryKafkaListener
    @KafkaListener(topics = ["retry_topic"])
    open fun retry(message: String) {
        println("Retrying message")
        throw RuntimeException("Bad things retried")
    }

}

fun main(args: Array<String>) {

}
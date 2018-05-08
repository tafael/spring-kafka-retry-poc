package br.com.zup.kafka.app.test

import com.nhaarman.mockito_kotlin.mock
import io.zup.springframework.kafka.annotation.RetryKafkaListener
import io.zup.springframework.kafka.annotation.RetryPolicy
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch

interface ConsumerHandler {
    fun onListen(message: String)
    fun onRetry(message: String)
    fun onDlq(message: String)
}

@Component
open class Consumer {

    var consumerHandler: ConsumerHandler? = null

    var latch = CountDownLatch(1)

    @RetryPolicy(topic = "retry_topic", retries = 3, dlqTopic = "dlq_topic")
    @KafkaListener(topics = ["main_topic"])
    open fun listen(message: String) {
        try {
            consumerHandler!!.onListen(message)
        } finally {
            latch.countDown()
        }
    }

    @RetryKafkaListener
    @KafkaListener(topics = ["retry_topic"])
    open fun retry(message: String) {
        try {
            consumerHandler!!.onRetry(message)
        } finally {
            latch.countDown()
        }
    }

    @KafkaListener(topics = ["dlq_topic"])
    open fun listenDLQ(message: String) {
        try {
            consumerHandler!!.onDlq(message)
        } finally {
            latch.countDown()
        }
    }

}

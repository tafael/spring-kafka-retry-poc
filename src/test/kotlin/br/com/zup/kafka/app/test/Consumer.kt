package br.com.zup.kafka.app.test

import io.zup.springframework.kafka.annotation.RetryKafkaListener
import io.zup.springframework.kafka.annotation.RetryPolicy
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

interface ConsumerHandler {
    fun onListen(message: String)
    fun onRetry(message: String)
    fun onDlq(message: String)
}

@Component
open class Consumer {

    var consumerHandler: ConsumerHandler? = null

    var latch = CountDownLatch(1)

    @RetryPolicy(id = "main_topic_id", topic = "retry_topic", retries = 3, dlqTopic = "dlq_topic")
    @KafkaListener(topics = ["main_topic"])
    open fun listen(message: String) {
        try {
            consumerHandler!!.onListen(message)
        } finally {
            latch.countDown()
        }
    }

    @RetryKafkaListener(retryPolicyId = "main_topic_id")
    @KafkaListener(topics = ["retry_topic"])
    open fun retry(message: ConsumerRecord<String, String>) {
        try {
            consumerHandler!!.onRetry(message.value())
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

    fun resetCount(count: Int) =
        CountDownLatch(count).let { latch = it }

    fun await() = latch.await(10, TimeUnit.SECONDS)

}


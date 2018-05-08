package br.com.zup.kafka.app.test

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.kafka.test.utils.ContainerTestUtils
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@SpringBootTest(classes = [TestApplicationConfig::class])
@RunWith(SpringRunner::class)
@DirtiesContext
class SpringKafkaRetryTest {

    companion object {

        val MAIN_TOPIC = "main_topic"
        val RETRY_TOPIC = "retry_topic"
        val DLQ_TOPIC = "dlq_topic"

        @ClassRule
        @JvmField
        val kafkaEmbedded = KafkaEmbedded(1, true,
            MAIN_TOPIC,
            RETRY_TOPIC,
            DLQ_TOPIC
        )

    }

    @Autowired
    lateinit var consumer: Consumer

    var consumerHandler: ConsumerHandler = mock()

    @Autowired
    lateinit var template: KafkaTemplate<String, String>

    @Autowired
    lateinit var kafkaListenerEndpointRegistry: KafkaListenerEndpointRegistry

    @Before
    fun setUp() {
        // wait until the partitions are assigned
        for (messageListenerContainer in kafkaListenerEndpointRegistry.listenerContainers) {
            ContainerTestUtils.waitForAssignment(
                messageListenerContainer,
                kafkaEmbedded.getPartitionsPerTopic()
            )
        }

        consumer.consumerHandler = consumerHandler
    }

    @Test
    fun `should succeed consuming at first attempt`() {
        val GOOD_MESSAGE = "good_message"

        // send the message
        sendAndWaitConsumer(GOOD_MESSAGE, 1)

        verify(consumerHandler, times(1)).onListen(GOOD_MESSAGE)
        assert(consumer.latch.count == 0L)

    }

    @Test
    fun `should retry consuming once and then succeed at second attempt`() {
        val BAD_MESSAGE = "bad_message"

        whenever(consumerHandler.onListen(BAD_MESSAGE)).thenThrow(RuntimeException(BAD_MESSAGE))

        sendAndWaitConsumer(BAD_MESSAGE, 2)

        verify(consumerHandler, times(1)).onListen(BAD_MESSAGE)
        verify(consumerHandler, times(1)).onRetry(any())
        assert(consumer.latch.count == 0L)
    }

    @Test
    fun `should retry consuming three times and then send to dlq`() {
        val BAD_MESSAGE = "bad_message"
        whenever(consumerHandler.onListen(BAD_MESSAGE)).thenThrow(RuntimeException("Bad message"))
        whenever(consumerHandler.onRetry(BAD_MESSAGE)).thenThrow(RuntimeException("Bad message"))

        sendAndWaitConsumer(BAD_MESSAGE, 5)

        verify(consumerHandler, times(1)).onListen(BAD_MESSAGE)
        verify(consumerHandler, times(3)).onRetry(any())
        verify(consumerHandler, times(1)).onDlq(any())

        assert(consumer.latch.count == 0L)

    }

    private fun sendAndWaitConsumer(message: String, count: Int) {
        consumer.latch = CountDownLatch(count)

        // send the message
        template.send(MAIN_TOPIC, message)

        consumer.latch.await(10, TimeUnit.SECONDS)
    }

}

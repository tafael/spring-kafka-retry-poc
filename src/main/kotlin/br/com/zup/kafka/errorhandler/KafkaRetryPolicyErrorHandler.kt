package br.com.zup.kafka.errorhandler

import br.com.zup.kafka.jackson.parseJson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.KafkaListenerErrorHandler
import org.springframework.kafka.listener.ListenerExecutionFailedException
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class KafkaRetryPolicyErrorHandler : KafkaListenerErrorHandler {

    @Autowired
    lateinit var template: KafkaTemplate<String, br.com.zup.kafka.framework.Message<String>>

    companion object {
        const val TOPIC = "retry_topic"
    }

    fun send(message: br.com.zup.kafka.framework.Message<String>) {
        template.send(TOPIC, message)
    }

    override fun handleError(message: Message<*>?, exception: ListenerExecutionFailedException?): Any {
        println("Handling error ${exception} with retry policy")

        val retryMessage: br.com.zup.kafka.framework.Message<String> =
            (message?.payload as String).parseJson(br.com.zup.kafka.framework.Message::class.java) as br.com.zup.kafka.framework.Message<String>

        if (retryMessage.retry > 0) {
            println("retry attempt ${retryMessage.retry} at ${retryMessage.timestamp} ")
            retryMessage.retry -= 1
            retryMessage.timestamp.plusMillis(1000)
            send(retryMessage)
            // retry
        } else {
            // dlq
            println("DLQ")
        }

        return retryMessage
    }

}

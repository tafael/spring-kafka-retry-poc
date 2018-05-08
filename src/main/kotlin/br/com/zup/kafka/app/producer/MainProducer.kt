package br.com.zup.kafka.app.producer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class MainProducer(val template: KafkaTemplate<String, String>) {

    companion object {
        const val TOPIC = "main_topic"
    }

    fun send(message: String) {
        template.send(TOPIC, message)
    }

}

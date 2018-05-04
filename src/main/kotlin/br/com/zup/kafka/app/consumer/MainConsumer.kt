package br.com.zup.kafka.app.consumer

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener

class MainConsumer {

    @KafkaListener(topics = ["main_topic"])
    fun listen(record: ConsumerRecord<Any, Any>) {

    }

}
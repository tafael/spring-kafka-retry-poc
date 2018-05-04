package br.com.zup.kafka.app.config

import br.com.zup.kafka.framework.Message
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
open class ProducerConfig {

    @Bean
    open fun producerFactory(): ProducerFactory<String, Message> =
        DefaultKafkaProducerFactory(producerConfigs())

    @Bean
    open fun producerConfigs(): Map<String, Any> =
        hashMapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
        )

    @Bean
    open fun kafkaTemplate(): KafkaTemplate<String, Message> =
        KafkaTemplate(producerFactory())

}
package br.com.zup.kafka.app.config

import br.com.zup.kafka.app.producer.MainProducer
import io.zup.springframework.kafka.config.KafkaRetryConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.kafka.config.KafkaListenerConfigUtils
import org.springframework.kafka.config.KafkaListenerEndpointRegistry


@SpringBootApplication(scanBasePackages = ["br.com.zup.kafka"])
@Import(KafkaRetryConfiguration::class)
open class SampleAppConfig : CommandLineRunner {

    @Autowired
    private lateinit var producer: MainProducer

    @Bean(name = [KafkaListenerConfigUtils.KAFKA_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME])
    open fun defaultKafkaListenerEndpointRegistry(): KafkaListenerEndpointRegistry = KafkaListenerEndpointRegistry()

    override fun run(vararg args: String?) {
        println("Running")
        repeat(1) { i ->
            producer.send("id = ${i}, body = ${"This is message #${i}"}")
        }

    }

}

package br.com.zup.kafka.app.test

import io.zup.springframework.kafka.config.KafkaRetryConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication()
@Import(KafkaRetryConfiguration::class)
open class TestApplicationConfig {
}

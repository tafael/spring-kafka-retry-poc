package br.com.zup.kafka.app.config

import br.com.zup.kafka.framework.Message
import br.com.zup.kafka.app.producer.MainProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import


@SpringBootApplication(scanBasePackages = ["br.com.zup.kafka"])
@Import(ProducerConfig::class)
open class SampleAppConfig : CommandLineRunner {

    @Autowired
    private lateinit var producer: MainProducer

    override fun run(vararg args: String?) {
        println("Running")
        repeat(10) {i ->
            producer.send(Message(id = i, body = "This is message #${i}"))
        }

    }


}
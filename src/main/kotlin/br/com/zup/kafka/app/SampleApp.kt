package br.com.zup.kafka.app

import br.com.zup.kafka.app.config.SampleAppConfig
import org.springframework.boot.SpringApplication

fun main(args: Array<String>) {
    SpringApplication.run(SampleAppConfig::class.java, *args)
}
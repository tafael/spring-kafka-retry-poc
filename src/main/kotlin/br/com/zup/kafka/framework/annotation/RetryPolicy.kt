package br.com.zup.kafka.framework.annotation

annotation class RetryPolicy(
    val topic: String,
    val retries: Int,
    val dlqTopic: String
)
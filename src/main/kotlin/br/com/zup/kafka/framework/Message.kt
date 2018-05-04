package br.com.zup.kafka.framework

import java.time.Instant

data class Message<T> (
    var id: Int,
    var body: T,
    var retry: Int = 0,
    var timestamp: Instant = Instant.now()
)
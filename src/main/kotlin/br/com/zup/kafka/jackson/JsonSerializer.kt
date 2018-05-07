package br.com.zup.kafka.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer
import org.springframework.util.Assert
import java.io.IOException

class JsonSerializer<T>(protected val objectMapper: ObjectMapper) : Serializer<T> {

    constructor() : this(JacksonExtension.jacksonObjectMapper) {
    }

    init {
        Assert.notNull(objectMapper, "'objectMapper' must not be null.")
    }

    override fun configure(configs: Map<String, *>, isKey: Boolean) {
        // No-op
    }

    override fun serialize(topic: String, data: T?): ByteArray? {
        try {
            var result: ByteArray? = null
            if (data != null) {
                result = this.objectMapper.writeValueAsBytes(data)
            }
            return result
        } catch (ex: IOException) {
            throw SerializationException("Can't serialize data [$data] for topic [$topic]", ex)
        }

    }

    override fun close() {
        // No-op
    }

}

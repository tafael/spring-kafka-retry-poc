package br.com.zup.kafka.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule


object JacksonExtension {

    val jacksonObjectMapper: ObjectMapper by lazy {
        ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .also {
                it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                it.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
                it.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
            }
    }

}

fun <T> String.parseJson(t: Class<T>): T =
    JacksonExtension.jacksonObjectMapper.readValue(this, t)

fun <T> T.toJson(): String =
    JacksonExtension.jacksonObjectMapper.writeValueAsString(this)

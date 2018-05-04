package br.com.zup.kafka.framework.consumer

import org.springframework.kafka.listener.MessageListenerContainer


class MessagePipeline(val baseTopicName: String) {

    private lateinit var messageListenerContainer: MessageListenerContainer

    fun withListener(listener: MessageListener) {
        messageListenerContainer = MessageListenerContainer()
    }

}
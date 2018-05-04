package br.com.zup.kafka.framework.consumer

import br.com.zup.kafka.framework.Message

interface MessageListener {

    fun onMessage(message: Message)

}
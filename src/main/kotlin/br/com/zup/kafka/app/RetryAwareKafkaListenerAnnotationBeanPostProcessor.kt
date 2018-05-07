package br.com.zup.kafka.app

import br.com.zup.kafka.errorhandler.KafkaRetryPolicyErrorHandler
import br.com.zup.kafka.framework.annotation.RetryKafkaListener
import br.com.zup.kafka.framework.annotation.RetryPolicy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor
import org.springframework.kafka.config.KafkaListenerConfigUtils
import org.springframework.kafka.config.MethodKafkaListenerEndpoint
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component(KafkaListenerConfigUtils.KAFKA_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)
open class RetryAwareKafkaListenerAnnotationBeanPostProcessor<K, V> : KafkaListenerAnnotationBeanPostProcessor<K, V>() {

    @Autowired
    private lateinit var template: KafkaTemplate<K, V>

    override fun processListener(
        endpoint: MethodKafkaListenerEndpoint<*, *>?,
        kafkaListener: KafkaListener?,
        bean: Any?,
        adminTarget: Any?,
        beanName: String?
    ) {

        val retryPolicy = AnnotationUtils.findAnnotation(endpoint?.method, RetryPolicy::class.java)
        val retryListener = AnnotationUtils.findAnnotation(endpoint?.method, RetryKafkaListener::class.java)

        KafkaRetryPolicyErrorHandler(template)
            .takeIf {
                retryPolicy != null || retryListener != null
            }
            ?.also { errorHandler ->
                retryPolicy
                    ?.apply { errorHandler.withRetryTopic(topic).withMaxRetries(retries) }
            }
            ?.let {
                endpoint?.setErrorHandler(it)
            }

        super.processListener(endpoint, kafkaListener, bean, adminTarget, beanName)

    }


}
package ch.barrierelos.backend.configuration

import ch.barrierelos.backend.constants.Queueing
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.support.RetryTemplate

@Configuration
public class QueueConfiguration(private val connectionFactory: ConnectionFactory)
{
  @Bean
  public fun exchange(): DirectExchange
  {
    return DirectExchange(Queueing.EXCHANGE_ANALYSIS)
  }

  @Bean
  public fun jobQueue(): Queue
  {
    return Queue(Queueing.QUEUE_JOB)
  }

  @Bean
  public fun resultQueue(): Queue
  {
    return Queue(Queueing.QUEUE_RESULT)
  }

  @Bean
  public fun jobBinding(exchange: DirectExchange, jobQueue: Queue): Binding
  {
    return BindingBuilder.bind(jobQueue).to(exchange).withQueueName()
  }

  @Bean
  public fun resultBinding(exchange: DirectExchange, resultQueue: Queue): Binding
  {
    return BindingBuilder.bind(resultQueue).to(exchange).withQueueName()
  }

  @Bean
  public fun rabbitTemplate(objectMapper: ObjectMapper): RabbitTemplate
  {
    val backOffPolicy = ExponentialBackOffPolicy()
    backOffPolicy.initialInterval = 500
    backOffPolicy.multiplier = 10.0
    backOffPolicy.maxInterval = 10000

    val retryTemplate = RetryTemplate()
    retryTemplate.setBackOffPolicy(backOffPolicy)

    val template = RabbitTemplate(this.connectionFactory)
    template.setRetryTemplate(retryTemplate)

    return template
  }
}

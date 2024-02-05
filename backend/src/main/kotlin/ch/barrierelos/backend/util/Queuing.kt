package ch.barrierelos.backend.util

import org.springframework.amqp.rabbit.core.RabbitTemplate

public fun RabbitTemplate.send(queue: String, json: String)
{
  this.convertAndSend(queue, json)
}

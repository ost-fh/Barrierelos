package ch.barrierelos.backend

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@AutoConfiguration
@EnableScheduling
@OpenAPIDefinition(info = Info(title = "Barrierelos API", version = "1.0.0", description = "API for the Barrierelos project"))
public class Application

public fun main(args: Array<String>)
{
  runApplication<Application>(*args)
}

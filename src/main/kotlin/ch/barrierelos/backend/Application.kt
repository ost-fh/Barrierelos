package ch.barrierelos.backend

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@AutoConfiguration
public class Application

public fun main(args: Array<String>)
{
  runApplication<Application>(*args)
}

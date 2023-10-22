package ch.barrierelos.backend.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
public class WebConfiguration : WebMvcConfigurer
{
  override fun addCorsMappings(registry: CorsRegistry)
  {
    registry.addMapping("/**").allowedMethods("*")
  }
}

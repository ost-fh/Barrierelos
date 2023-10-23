package ch.barrierelos.backend.configuration

import ch.barrierelos.backend.message.enums.Order
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.*


@Configuration
public class WebConfiguration : WebMvcConfigurer
{
  override fun addCorsMappings(registry: CorsRegistry)
  {
    registry.addMapping("/**").allowedMethods("*")
  }

  override fun addFormatters(registry: FormatterRegistry)
  {
    registry.addConverter(StringToOrderConverter())
  }

  private class StringToOrderConverter : Converter<String, Order>
  {
    override fun convert(source: String): Order?
    {
      return try
      {
        Order.valueOf(source.uppercase(Locale.getDefault()))
      }
      catch(exception: Exception)
      {
        null
      }
    }
  }
}

package ch.barrierelos.backend.configuration

import ch.barrierelos.backend.message.enums.OrderEnum
import ch.barrierelos.backend.model.enums.RoleEnum
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
    registry.addConverter(StringToOrderEnumConverter())
    registry.addConverter(StringToRoleEnumConverter())
  }

  private class StringToOrderEnumConverter : Converter<String, OrderEnum>
  {
    override fun convert(source: String): OrderEnum
    {
      return OrderEnum.valueOf(source.uppercase(Locale.getDefault()))
    }
  }

  private class StringToRoleEnumConverter : Converter<String, RoleEnum>
  {
    override fun convert(source: String): RoleEnum
    {
      return RoleEnum.valueOf(source.uppercase(Locale.getDefault()))
    }
  }
}

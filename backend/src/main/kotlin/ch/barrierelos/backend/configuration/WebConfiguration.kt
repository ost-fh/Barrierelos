package ch.barrierelos.backend.configuration

import ch.barrierelos.backend.enums.*
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
    registry.addConverter(StringToStatusEnumConverter())
    registry.addConverter(StringToStateEnumConverter())
    registry.addConverter(StringToReasonEnumConverter())
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

  private class StringToStatusEnumConverter : Converter<String, StatusEnum>
  {
    override fun convert(source: String): StatusEnum
    {
      return StatusEnum.valueOf(source.uppercase(Locale.getDefault()))
    }
  }

  private class StringToStateEnumConverter : Converter<String, StateEnum>
  {
    override fun convert(source: String): StateEnum
    {
      return StateEnum.valueOf(source.uppercase(Locale.getDefault()))
    }
  }

  private class StringToReasonEnumConverter : Converter<String, ReasonEnum>
  {
    override fun convert(source: String): ReasonEnum
    {
      return ReasonEnum.valueOf(source.uppercase(Locale.getDefault()))
    }
  }
}

package ch.barrierelos.backend.configuration

import ch.barrierelos.backend.constants.Endpoint.USER
import ch.barrierelos.backend.constants.Endpoint.USER_ROLE
import ch.barrierelos.backend.model.enums.RoleEnum
import ch.barrierelos.backend.security.AuthenticationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
public class SecurityConfiguration
{
  @Bean
  public fun securityFilterChain(http: HttpSecurity): SecurityFilterChain
  {
    return http
      .csrf { csrf: CsrfConfigurer<HttpSecurity> -> csrf.disable() }
      .cors(withDefaults())
      .authorizeHttpRequests { authorize ->
        authorize
          .requestMatchers("$USER/**").hasAnyRole(RoleEnum.ADMIN.name)
          .requestMatchers("$USER_ROLE/**").hasAnyRole(RoleEnum.ADMIN.name)
          .anyRequest().denyAll()
      }
      .httpBasic(withDefaults())
      .build()
  }
  
  @Bean
  public fun userDetailsService(): AuthenticationService = AuthenticationService()
  
  @Bean
  public fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()
  
  @Bean
  public fun corsConfigurationSource(): CorsConfigurationSource
  {
    val configuration = CorsConfiguration()
    configuration.allowedOrigins = listOf("*")
    configuration.allowedMethods = listOf("HEAD", "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
    configuration.allowedHeaders = listOf("*")
    configuration.exposedHeaders = listOf("x-auth-token")
    
    val source = UrlBasedCorsConfigurationSource()
    source.registerCorsConfiguration("/**", configuration)
    
    return source
  }
}

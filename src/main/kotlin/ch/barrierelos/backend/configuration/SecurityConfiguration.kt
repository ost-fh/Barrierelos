package ch.barrierelos.backend.configuration

import ch.barrierelos.backend.constants.Endpoint.WEBSITES
import ch.barrierelos.backend.constants.Endpoint.CREDENTIAL
import ch.barrierelos.backend.constants.Endpoint.DOCUMENTATION_OPENAPI
import ch.barrierelos.backend.constants.Endpoint.DOCUMENTATION_SWAGGER
import ch.barrierelos.backend.constants.Endpoint.USER
import ch.barrierelos.backend.constants.Endpoint.STATISTICS
import ch.barrierelos.backend.constants.Endpoint.TAG
import ch.barrierelos.backend.enums.RoleEnum
import ch.barrierelos.backend.security.AuthenticationConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
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
  @Autowired
  private lateinit var authenticationConverter: AuthenticationConverter

  private fun HttpSecurity.configure(): HttpSecurity
  {
    return this
      .csrf { csrf: CsrfConfigurer<HttpSecurity> -> csrf.disable() }
      .cors(withDefaults())
      .authorizeHttpRequests { authorize ->
        authorize
          .requestMatchers("$WEBSITES/**").permitAll()
          .requestMatchers(HttpMethod.POST, "$USER/**").permitAll()
          .requestMatchers(HttpMethod.PUT, "$USER/**").hasAnyRole(RoleEnum.ADMIN.name, RoleEnum.MODERATOR.name, RoleEnum.CONTRIBUTOR.name, RoleEnum.VIEWER.name)
          .requestMatchers(HttpMethod.HEAD, "$USER/**").hasAnyRole(RoleEnum.ADMIN.name, RoleEnum.MODERATOR.name, RoleEnum.CONTRIBUTOR.name, RoleEnum.VIEWER.name)
          .requestMatchers(HttpMethod.GET, "$USER/**").hasAnyRole(RoleEnum.ADMIN.name, RoleEnum.MODERATOR.name, RoleEnum.CONTRIBUTOR.name, RoleEnum.VIEWER.name)
          .requestMatchers(HttpMethod.DELETE, "$USER/**").hasAnyRole(RoleEnum.ADMIN.name, RoleEnum.MODERATOR.name, RoleEnum.CONTRIBUTOR.name, RoleEnum.VIEWER.name)
          .requestMatchers(HttpMethod.POST, "$TAG/**").hasAnyRole(RoleEnum.ADMIN.name)
          .requestMatchers(HttpMethod.PUT, "$TAG/**").hasAnyRole(RoleEnum.ADMIN.name)
          .requestMatchers(HttpMethod.HEAD, "$TAG/**").permitAll()
          .requestMatchers(HttpMethod.GET, "$TAG/**").permitAll()
          .requestMatchers(HttpMethod.DELETE, "$TAG/**").hasAnyRole(RoleEnum.ADMIN.name)
          .requestMatchers("$STATISTICS/**").permitAll()
          .requestMatchers(HttpMethod.PUT, "$CREDENTIAL/**").hasAnyRole(RoleEnum.ADMIN.name, RoleEnum.MODERATOR.name, RoleEnum.CONTRIBUTOR.name, RoleEnum.VIEWER.name)
          .requestMatchers("$DOCUMENTATION_OPENAPI/**").permitAll()
          .requestMatchers("$DOCUMENTATION_OPENAPI.yaml").permitAll()
          .requestMatchers("$DOCUMENTATION_SWAGGER/**").permitAll()
          .anyRequest().denyAll()
      }
  }

  @Bean
  @Order(Ordered.LOWEST_PRECEDENCE)
  public fun basicAuthFilterChain(http: HttpSecurity): SecurityFilterChain
  {
    return http
      .configure()
      .httpBasic(withDefaults())
      .build()
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public fun oAuthFilterChain(http: HttpSecurity): SecurityFilterChain
  {
    return http
      .securityMatcher { request ->
        request.getHeader(HttpHeaders.AUTHORIZATION)?.startsWith("Bearer ", true) ?: false
      }
      .configure()
      .oauth2ResourceServer { oauth2 ->
        oauth2.jwt { jwt ->
          jwt.jwtAuthenticationConverter(this.authenticationConverter)
        }
      }
      .build()
  }

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

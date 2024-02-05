import ch.barrierelos.backend.util.fromJson
import org.springframework.test.web.servlet.ResultActionsDsl

inline fun <reified T> ResultActionsDsl.body(): T
{
  return this.andReturn().response.contentAsString.fromJson()
}

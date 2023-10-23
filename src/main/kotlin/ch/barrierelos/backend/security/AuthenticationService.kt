package ch.barrierelos.backend.security

import ch.barrierelos.backend.entity.UserEntity
import ch.barrierelos.backend.entity.UserRoleEntity
import ch.barrierelos.backend.repository.UserRepository
import ch.barrierelos.backend.repository.UserRoleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

public class AuthenticationService : UserDetailsService
{
  @Autowired
  private lateinit var userRepository: UserRepository
  @Autowired
  private lateinit var userRoleRepository: UserRoleRepository
  
  override fun loadUserByUsername(username: String): UserDetails
  {
    val user: UserEntity? = this.userRepository.findByUsername(username)
    
    return if(user == null)
    {
      throw UsernameNotFoundException("Username $username not found.")
    }
    else
    {
      val roles: List<UserRoleEntity> = this.userRoleRepository.findAllByUserFk(user.userId)
      
      AuthenticationDetails(user.userId, user.username, user.password, roles.map { it.role })
    }
  }
}

package github.ijkzen.blog.config.security


import github.ijkzen.blog.service.DeveloperService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

/**
 * @Author ijkzen
 * @Date 2019/9/29
 */
@Component
class DeveloperAuthenticationProvider : AuthenticationProvider {

    @Autowired
    private lateinit var developerService: DeveloperService

    override fun authenticate(authentication: Authentication?): Authentication {
        val nodeId = authentication!!.name
        authentication.isAuthenticated = developerService.searchDeveloperByNodeId(nodeId) != null

        return authentication
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return authentication!! == UsernamePasswordAuthenticationToken::class.java
    }

}
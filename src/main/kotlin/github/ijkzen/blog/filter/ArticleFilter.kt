package github.ijkzen.blog.filter

import github.ijkzen.blog.bean.BaseBean
import github.ijkzen.blog.utils.getAuthentication
import org.codehaus.jackson.map.ObjectMapper
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @Author ijkzen
 * @Date 2019/9/29
 */
class ArticleFilter(url: String, authenticationManager: AuthenticationManager) : AbstractAuthenticationProcessingFilter(AntPathRequestMatcher(url)) {

    init {
        setAuthenticationManager(authenticationManager)
    }

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        val authentication = getAuthentication(request!!)
        return if (authentication == null) {
            throw UsernameNotFoundException("认证失败")
        } else {
            authenticationManager.authenticate(authentication)
        }
    }

    override fun unsuccessfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, failed: AuthenticationException?) {
        System.err.println("认证失败")
        SecurityContextHolder.clearContext();
        response!!.contentType = "application/json"
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        val result = BaseBean("403", "认证失败")
        response.outputStream.println(ObjectMapper().writeValueAsString(result))
    }
}
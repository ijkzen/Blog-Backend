package github.ijkzen.blog.filter

import github.ijkzen.blog.bean.BaseBean
import github.ijkzen.blog.utils.getAuthorization
import github.ijkzen.blog.utils.setAuthentication
import org.codehaus.jackson.map.ObjectMapper
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import javax.servlet.FilterChain
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
        setContinueChainBeforeSuccessfulAuthentication(true)
        val authentication = getAuthorization(request!!)
        return if (authentication == null) {
            throw UsernameNotFoundException("认证失败")
        } else {
            val result = authenticationManager.authenticate(authentication)
            setAuthentication(result)
            result
        }
    }

    override fun successfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?, authResult: Authentication?) {
        super.successfulAuthentication(request, response, chain, authResult)
        System.err.println("认证成功")
    }

    override fun unsuccessfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, failed: AuthenticationException?) {
        System.err.println("认证失败")
        SecurityContextHolder.clearContext();
        response!!.contentType = "application/json;charset=UTF-8"
        response.status = HttpServletResponse.SC_OK
        response.characterEncoding = "UTF-8"
        val result = BaseBean("401", "认证失败")
        response.writer.println(ObjectMapper().writeValueAsString(result))
    }
}
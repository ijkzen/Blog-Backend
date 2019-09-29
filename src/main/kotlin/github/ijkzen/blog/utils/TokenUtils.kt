package github.ijkzen.blog.utils

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import javax.servlet.http.HttpServletRequest

/**
 * @Author ijkzen
 * @Date 2019/9/29
 */

const val AUTHORIZATION = "Authorization"

fun getAuthentication(request: HttpServletRequest): Authentication? {
    val token = request.getHeader(AUTHORIZATION)
    return if (token == null) {
        null
    } else {
        UsernamePasswordAuthenticationToken(token, null, null)
    }
}
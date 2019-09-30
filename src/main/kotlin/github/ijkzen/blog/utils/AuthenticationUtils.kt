package github.ijkzen.blog.utils

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

/**
 * @Author ijkzen
 * @Date 2019/9/30
 */
fun getAuthentication(): Authentication? = SecurityContextHolder.getContext().authentication

fun setAuthentication(authentication: Authentication) {
    SecurityContextHolder.getContext().authentication = authentication
}

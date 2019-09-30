package github.ijkzen.blog.utils

import github.ijkzen.blog.bean.BaseBean
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

fun unAuthorized(result: BaseBean): BaseBean {
    result.errCode = "401"
    result.errMessage = "认证失败"
    return result
}

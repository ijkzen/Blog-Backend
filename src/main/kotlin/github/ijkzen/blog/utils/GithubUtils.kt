package github.ijkzen.blog.utils

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.util.*

/**
 * @Author ijkzen
 * @Date 2019/9/25
 */

fun getGithubHeaders(token: String): HttpHeaders {
    val headers = HttpHeaders()
    return headers.apply {
        accept = Collections.singletonList(MediaType.APPLICATION_JSON)
        set("Authorization", "token $token")
    }
}
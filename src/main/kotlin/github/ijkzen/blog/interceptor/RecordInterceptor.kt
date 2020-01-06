package github.ijkzen.blog.interceptor

import github.ijkzen.blog.service.RecordService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class RecordInterceptor : HandlerInterceptor {

    @Autowired
    private lateinit var recordService: RecordService

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        recordService.saveRecord(request)
        return true
    }
}
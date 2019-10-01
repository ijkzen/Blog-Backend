package github.ijkzen.blog.filter

import github.ijkzen.blog.service.RecordService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest

@Component
@WebFilter(urlPatterns = ["/**"], filterName = "record")
class RecordFilter : GenericFilterBean() {

    @Autowired
    private lateinit var recordService: RecordService

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        recordService.saveRecord(request as HttpServletRequest)
        chain!!.doFilter(request, response)
    }

}
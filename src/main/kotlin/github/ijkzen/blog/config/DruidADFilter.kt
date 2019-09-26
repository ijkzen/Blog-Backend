package github.ijkzen.blog.config

import com.alibaba.druid.util.Utils
import javax.servlet.*

class DruidADFilter : Filter {
    private val filePath = "support/http/resources/js/common.js"

    companion object {
        private const val ORIGIN_JS = "this.buildFooter();"
        private const val NEW_JS = "//this.buildFooter();"
    }

    override fun init(filterConfig: FilterConfig?) {

    }

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        chain!!.doFilter(request, response)
        response!!.resetBuffer()
        var text = Utils.readFromResource(filePath)
        text = text.replace(ORIGIN_JS, NEW_JS)
        response.writer.write(text)
    }
}
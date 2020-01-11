package github.ijkzen.blog.config

import github.ijkzen.blog.interceptor.RecordInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class InterceptorConfig : WebMvcConfigurer {

    @Autowired
    private lateinit var recordInterceptor: RecordInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(recordInterceptor).addPathPatterns("/**")
    }
}
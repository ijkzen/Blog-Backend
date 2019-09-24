package github.ijkzen.blog.config

import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean

//@Configuration
//@AutoConfigureAfter(DruidDataSourceAutoConfigure::class)
class RemoveDruidADConfig {

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(
            name = ["spring.datasource.druid.stat-view-servlet.enabled"],
            havingValue = "true"
    )
    fun removeDruidAD(druidStatProperties: DruidStatProperties): FilterRegistrationBean<DruidADFilter> {
        val config = druidStatProperties.statViewServlet
        val pattern = if (config.urlPattern != null) config.urlPattern else "/druid/*"
        val commonJsPattern = pattern.replace("\\*", "js/common.js")
        val registrationBean = FilterRegistrationBean<DruidADFilter>()
        registrationBean.filter = DruidADFilter()
        registrationBean.addUrlPatterns(commonJsPattern)
        return registrationBean
    }
}
package github.ijkzen.blog.config

import com.alibaba.druid.pool.DruidDataSource
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class DruidConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.druid")
    fun dataSource(): DruidDataSource {
        return DruidDataSourceBuilder.create().build()
    }

}
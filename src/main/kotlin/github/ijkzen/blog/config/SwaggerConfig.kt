package github.ijkzen.blog.config

import github.ijkzen.blog.utils.DOMAIN
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

/**
 * @Author ijkzen
 * @Date 2019/9/27
 */

@Configuration
@EnableSwagger2
class SwaggerConfig {

    companion object {
        const val NAME = "IJKZEN"
        const val URL = "https://github.com/ijkzen"
        const val EMAIL = "ijkzen@outlook.com"
    }

    @Bean
    fun createSwagger(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .host(DOMAIN.replace("https://", "").replace("http://", ""))
            .apiInfo(createSwaggerInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("github.ijkzen.blog.controller"))
            .paths(PathSelectors.any())
            .build()
    }

    fun createSwaggerInfo(): ApiInfo {
        return ApiInfoBuilder()
            .title("NextTo-Blog 后台接口文档")
            .description("描述博客接口")
            .contact(Contact(NAME, URL, EMAIL))
            .version("0.0.1")
            .build()
    }
}
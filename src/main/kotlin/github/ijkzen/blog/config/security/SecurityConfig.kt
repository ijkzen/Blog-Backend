package github.ijkzen.blog.config.security

import github.ijkzen.blog.filter.CommonFilter
import github.ijkzen.blog.utils.HTTP_DELETE
import github.ijkzen.blog.utils.HTTP_GET
import github.ijkzen.blog.utils.HTTP_POST
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


/**
 * @Author ijkzen
 * @Date 2019/9/29
 */
@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var authenticationProvider: DeveloperAuthenticationProvider

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.authenticationProvider(authenticationProvider)
    }

    override fun configure(http: HttpSecurity?) {
        http!!.cors()
            .and()
            .csrf().disable()
            .addFilterBefore(
                CommonFilter("/article/*", authenticationManager()),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                CommonFilter("/comment/*", authenticationManager(), HTTP_DELETE),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                CommonFilter("/comment/report/list", authenticationManager(), HTTP_GET),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                CommonFilter("/mail/new", authenticationManager(), HTTP_POST),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                CommonFilter("/donate/*", authenticationManager(), HTTP_POST),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                CommonFilter("/chat/*", authenticationManager(), HTTP_GET),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                CommonFilter("/developer/info", authenticationManager(), HTTP_GET),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                CommonFilter("/about/me", authenticationManager(), HTTP_POST),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                CommonFilter("/developer/master", authenticationManager(), HTTP_GET),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                CommonFilter("/oss/set", authenticationManager(), HTTP_POST),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                CommonFilter("/ssh/new", authenticationManager(), HTTP_POST),
                UsernamePasswordAuthenticationFilter::class.java
            ).addFilterBefore(
                CommonFilter("/comment/batchDelete", authenticationManager(), HTTP_POST),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                CommonFilter("/comment/batchCancel", authenticationManager(), HTTP_POST),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                CommonFilter("/article/apply/*", authenticationManager(), HTTP_GET),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                CommonFilter("/article/cancel/*", authenticationManager(), HTTP_GET),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                CommonFilter("/article/edit/list", authenticationManager(), HTTP_GET),
                UsernamePasswordAuthenticationFilter::class.java
            )
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("authorization", "content-type")
        configuration.exposedHeaders = listOf("Authorization")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
package github.ijkzen.blog.config.security

import github.ijkzen.blog.filter.CommonFilter
import github.ijkzen.blog.utils.HTTP_DELETE
import github.ijkzen.blog.utils.HTTP_GET
import github.ijkzen.blog.utils.HTTP_POST
import org.hibernate.SessionFactory
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
import javax.persistence.EntityManagerFactory


/**
 * @Author ijkzen
 * @Date 2019/9/29
 */
@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var authenticationProvider: DeveloperAuthenticationProvider

    @Autowired
    private lateinit var entityManagerFactory: EntityManagerFactory


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
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("authorization", "content-type", "x-auth-token")
        configuration.exposedHeaders = listOf("x-auth-token", "Authorization")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun sessionFactory(): SessionFactory {
        return entityManagerFactory.unwrap(SessionFactory::class.java)
    }
}
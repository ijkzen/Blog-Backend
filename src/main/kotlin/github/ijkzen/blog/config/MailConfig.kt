package github.ijkzen.blog.config

import github.ijkzen.blog.repository.MailRepository
import github.ijkzen.blog.utils.SecurityUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

/**
 * @Author ijkzen
 * @Date 2019/10/6
 */
@Configuration
class MailConfig {

    @Autowired
    private lateinit var mailRepository: MailRepository

    @Bean
    fun getJavaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        val mail = mailRepository.findMailByInUseTrue()
        mailSender.host = mail.host
        mailSender.port = mail.port
        mailSender.username = mail.userName
        mailSender.password = SecurityUtils.decryption(mail.password)
        mailSender.javaMailProperties.let {
            it["mail.transport.protocol"] = "smtp"
            it["mail.smtp.auth"] = "true"
            it.put("mail.smtp.starttls.enable", if (mail.startTls) "true" else "false")
        }
        return mailSender
    }
}
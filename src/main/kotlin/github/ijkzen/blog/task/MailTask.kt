package github.ijkzen.blog.task

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component

@Component
class MailTask {

    @Autowired
    private lateinit var mailSenderImpl: JavaMailSenderImpl

    fun sendMail() {
        val message = SimpleMailMessage()
        message.setFrom("ijkzen@outlook.com")
        message.setTo("krystalandhola@outlook.com")
        message.setText("are you ok?")
        mailSenderImpl.send(message)
    }

}
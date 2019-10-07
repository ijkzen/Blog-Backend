package github.ijkzen.blog.task

import github.ijkzen.blog.repository.MailRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class MailTask {

    @Autowired
    private lateinit var mailSender: JavaMailSender

    @Autowired
    private lateinit var mailRepository: MailRepository

    fun sendMail(receiver: String, text: String) {
        val config = mailRepository.findMailByInUseTrue()
        val message = SimpleMailMessage()
        message.setFrom(config.userName)
        message.setTo(receiver)
        message.setText(text)
        mailSender.send(message)
    }

}
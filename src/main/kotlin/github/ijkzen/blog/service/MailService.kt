package github.ijkzen.blog.service

import github.ijkzen.blog.bean.mail.MailConfigurationBean
import github.ijkzen.blog.repository.MailRepository
import github.ijkzen.blog.utils.SecurityUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart


/**
 * @Author ijkzen
 * @Date 2019/10/6
 */
@Service
class MailService {

    @Autowired
    private lateinit var mailRepository: MailRepository

    private val logger = LoggerFactory.getLogger(javaClass)

    fun save(mail: MailConfigurationBean) {
        mailRepository.deleteUseless()
        mailRepository.save(
                mail.apply {
                    password = SecurityUtils.encryption(password)
                }
        )
    }

    fun sendMail(receiver: String, subject: String, text: String) {
        val config = mailRepository.findMailByInUseTrue()
        if (config == null) {
            logger.error("please configure mail ")
        } else {
            val properties = Properties()
            properties.let {
                it["mail.smtp.starttls.enable"] = config.startTls
                it["mail.smtp.host"] = config.host
                it["mail.smtp.port"] = config.port
                it["mail.transport.protocol"] = "smtp"
                it["mail.smtp.auth"] = "true"
                it["mail.smtp.starttls.enable"] = if (config.startTls) "true" else "false"
            }

            val session: Session = Session.getInstance(
                    properties,
                    object : Authenticator() {
                        override fun getPasswordAuthentication(): PasswordAuthentication {
                            return PasswordAuthentication(
                                    config.userName,
                                    SecurityUtils.decryption(config.password)
                            )
                        }
                    }
            )

            val message = MimeMessage(session)
            message.setFrom(config.userName)
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(receiver)
            )
            message.subject = subject
            val mimeBodyPart = MimeBodyPart()
            mimeBodyPart.setContent(text, "text/plain; charset=UTF-8")
            val multipart = MimeMultipart()
            multipart.addBodyPart(mimeBodyPart)
            message.setContent(multipart)
            Transport.send(message)
        }
    }
}
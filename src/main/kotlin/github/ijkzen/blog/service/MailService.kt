package github.ijkzen.blog.service

import github.ijkzen.blog.bean.mail.Mail
import github.ijkzen.blog.repository.MailRepository
import github.ijkzen.blog.utils.SecurityUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @Author ijkzen
 * @Date 2019/10/6
 */
@Service
class MailService {

    @Autowired
    private lateinit var mailRepository: MailRepository

    private val logger = LoggerFactory.getLogger(javaClass)

    fun save(mail: Mail) {
        mailRepository.deleteUseless()
        mailRepository.save(
                mail.apply {
                    password = SecurityUtils.encryption(password)
                }
        )
    }

    fun sendMail(receiver: String, text: String) {
        val config = mailRepository.findMailByInUseTrue()
        if (config == null) {
            logger.error("please configure mail ")
        }
    }
}
package github.ijkzen.blog.service

import github.ijkzen.blog.bean.mail.Mail
import github.ijkzen.blog.repository.MailRepository
import github.ijkzen.blog.utils.SecurityUtils
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

    fun save(mail: Mail) {
        mailRepository.deleteUseless()
        mailRepository.save(
                mail.apply {
                    password = SecurityUtils.encryption(password)
                }
        )
    }
}
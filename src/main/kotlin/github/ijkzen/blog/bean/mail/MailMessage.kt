package github.ijkzen.blog.bean.mail

/**
 * @Author ijkzen
 * @Date 2019/10/9
 */

data class MailMessage(
        var receiver: String,
        var subject: String,
        var text: String
)
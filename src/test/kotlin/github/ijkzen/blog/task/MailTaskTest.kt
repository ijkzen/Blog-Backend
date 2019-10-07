package github.ijkzen.blog.task

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class MailTaskTest {
    @Autowired
    private lateinit var mailTask: MailTask

    @Test
    fun sendMail() {
        mailTask.sendMail(
                "krystalandhola@outlook.com",
                "2019/10/08 hello world"
        )

    }
}
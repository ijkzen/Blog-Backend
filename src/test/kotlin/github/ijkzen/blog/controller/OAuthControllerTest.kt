package github.ijkzen.blog.controller

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@SpringBootTest
class OAuthControllerTest {

    @Autowired
    private lateinit var oAuthController: OAuthController

    @Test
    fun isExistRepository() {
        Assert.assertTrue(oAuthController.isExistRepository())
    }


}
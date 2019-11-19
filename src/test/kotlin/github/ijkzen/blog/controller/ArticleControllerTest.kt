package github.ijkzen.blog.controller

import github.ijkzen.blog.service.ArticleService
import github.ijkzen.blog.service.RecordService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration


@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
@RunWith(SpringRunner::class)
class ArticleControllerTest {

    @Autowired
    private lateinit var articleService: ArticleService

    @Autowired
    private lateinit var recordService: RecordService

    @Test
    fun getCategories() {
        articleService.getCategories()
    }

    @Test
    fun getPeopleCount() {
        println(recordService.getPeopleCount())
    }


}
package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.BaseBean
import github.ijkzen.blog.bean.articles.Article
import github.ijkzen.blog.utils.AUTHORIZATION
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.*


@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
@RunWith(SpringRunner::class)
class ArticleControllerTest {


    private lateinit var mock: MockMvc

    @Autowired
    private lateinit var context: WebApplicationContext

    private val objectMapper = ObjectMapper()

    @Before
    fun initMock() {
        mock = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun addGuestArticle() {
        val article = Article(
                null,
                "2019-10-12-test.md",
                "ijkzen",
                true,
                false,
                "test",
                "test",
                0,
                null,
                Date(),
                Date(),
                "test",
                "test"

        )

        val result = mock.perform(
                MockMvcRequestBuilders.post("/article/new")
                        .authorization()
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(article))
        )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()

        assertEquals("000", objectMapper.readValue(result.response.contentAsString, BaseBean::class.java).errCode)
    }

    @Test
    fun deleteArticle() {
    }

    @Test
    fun editArticle() {
    }


}

fun MockHttpServletRequestBuilder.authorization(): MockHttpServletRequestBuilder {
    this.header(AUTHORIZATION, "MDQ6VXNlcjMxNTMxODM2")
    return this
}
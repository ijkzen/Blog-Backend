package github.ijkzen.blog

import github.ijkzen.blog.service.GitService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class BlogApplicationTests {

    @Autowired
    private lateinit var gitService: GitService

    @Test
    fun deleteRepo() {
        assert(gitService.deleteRemoteRepository())
    }

}

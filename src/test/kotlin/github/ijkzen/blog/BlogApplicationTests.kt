package github.ijkzen.blog

import github.ijkzen.blog.service.GitService
import github.ijkzen.blog.service.OSSService
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

    @Autowired
    private lateinit var oSSService: OSSService

//    @Test
//    fun deleteRepo() {
//        assert(gitService.deleteRemoteRepository())
//    }

    @Test
    fun uploadImagesTest() {
        oSSService.uploadAllImages()
    }

}

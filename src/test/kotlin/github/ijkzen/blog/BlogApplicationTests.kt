package github.ijkzen.blog

import github.ijkzen.blog.service.ArticleService
import github.ijkzen.blog.service.GitService
import github.ijkzen.blog.service.OSSService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.io.File

@RunWith(SpringRunner::class)
@SpringBootTest
class BlogApplicationTests {

    @Autowired
    private lateinit var gitService: GitService

    @Autowired
    private lateinit var oSSService: OSSService

    @Autowired
    private lateinit var articleService: ArticleService

//    @Test
//    fun deleteRepo() {
//        assert(gitService.deleteRemoteRepository())
//    }

//    @Test
//    fun uploadImagesTest() {
//        oSSService.uploadAllImages()
//    }

    @Test
    fun getArticleMetaTest() {
        val markdown = File("D:\\Projects\\books\\ijkzen.github.io\\_posts\\2019-09-04-算法导论4_1.md").readText()

        System.err.println(articleService.getTitle(markdown, File("D:\\Projects\\books\\ijkzen.github.io\\_posts\\2019-09-04-算法导论4_1.md")))
        System.err.println(articleService.getCategories(markdown))
        //System.err.println(articleService.getAuthor(markdown))
        System.err.println(articleService.replaceUrl(markdown))
    }


}

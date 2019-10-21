package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.BaseBean
import github.ijkzen.blog.bean.articles.Article
import github.ijkzen.blog.bean.articles.NewArticle
import github.ijkzen.blog.service.ArticleService
import github.ijkzen.blog.service.DeveloperService
import github.ijkzen.blog.service.NewArticleService
import github.ijkzen.blog.utils.AUTHORIZATION
import github.ijkzen.blog.utils.getAuthentication
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * @Author ijkzen
 * @Date 2019/9/27
 */

@Api(
        value = "修改文章",
        tags = ["修改文章"],
        description = "需要检查权限"
)
@RequestMapping(value = ["/article"])
@RestController
class ArticleController {

    @Autowired
    private lateinit var articleService: ArticleService

    @Autowired
    private lateinit var newArticleService: NewArticleService

    @Autowired
    private lateinit var developerService: DeveloperService

    @ApiOperation(
            value = "访客添加文章",
            notes =
            """
                这会对表的内容进行修改，所以需要在header的Authorization字段写入开发者的nodeId            
            """

    )
    @ApiImplicitParams(
            ApiImplicitParam(
                    name = "article",
                    value = "文章主体",
                    required = true,
                    dataType = "Article",
                    dataTypeClass = Article::class,
                    paramType = "body"
            ),
            ApiImplicitParam(
                    name = AUTHORIZATION,
                    value = "验证身份",
                    required = true,
                    dataTypeClass = String::class,
                    paramType = "header"
            )
    )
    @PostMapping(value = ["/new"])
    fun addGuestArticle(@RequestBody article: Article): BaseBean {
        val result = BaseBean()
        article.let {
            it.deleted = false
            it.shown = false
            it.id = null
        }
        val articleId = articleService.save(article)
        result.errMessage = articleId.id.toString()
        return result
    }


    @ApiOperation(
            value = "删除文章",
            notes =
            """
                根据Id删除文章
            """
    )
    @ApiImplicitParams(
            ApiImplicitParam(
                    name = AUTHORIZATION,
                    value = "验证身份",
                    required = true,
                    dataTypeClass = String::class,
                    paramType = "header"
            ),
            ApiImplicitParam(
                    name = "id",
                    value = "将要被删除的文章Id",
                    required = true,
                    dataTypeClass = Long::class
            )
    )
    @DeleteMapping(value = ["/{id}"])
    fun deleteArticle(@PathVariable("id") id: Long): BaseBean {
        val authentication = getAuthentication()
        val master = developerService.searchMaster()
        val result = BaseBean()

        return if (authentication!!.principal == master.nodeId) {
            articleService.deleteArticle(id)
            result.apply {
                errMessage = "删除成功"
            }
        } else {
            val developer = developerService.searchDeveloperByNodeId(authentication.principal as String)
            val article = articleService.getArticle(id).get()
            if (article.author == developer!!.developerName) {
                articleService.deleteArticle(id)
                result.apply {
                    errMessage = "删除成功"
                }
            } else {
                result.errCode = "403"
                result.apply {
                    errMessage = "无操作权限"
                }
            }
        }
    }

    //todo test
    @ApiOperation(
            value = "修改文章记录",
            notes =
            """
                添加文章修改记录，等待站长确认            
            """
    )
    @ApiImplicitParams(
            ApiImplicitParam(
                    name = AUTHORIZATION,
                    value = "验证身份",
                    required = true,
                    dataTypeClass = String::class,
                    paramType = "header"
            ),
            ApiImplicitParam(
                    name = "newArticle",
                    value = "修改记录",
                    required = true,
                    dataType = "NewArticle",
                    dataTypeClass = NewArticle::class,
                    paramType = "body"
            )
    )
    @PostMapping(value = ["/edit"])
    fun editArticle(@RequestBody newArticle: NewArticle): BaseBean {
        val result = BaseBean()
        newArticleService.save(
                newArticle.apply {
                    id = null
                }
        )
        return result
    }
}
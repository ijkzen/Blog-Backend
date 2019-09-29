package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.BaseBean
import github.ijkzen.blog.bean.articles.Article
import github.ijkzen.blog.bean.articles.NewArticle
import github.ijkzen.blog.service.ArticleService
import github.ijkzen.blog.service.NewArticleService
import github.ijkzen.blog.utils.AUTHORIZATION
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
                    dataTypeClass = Article::class
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
        article.isDelete = false
        article.isShow = false
        val articleId = articleService.save(article)
        result.errMessage = articleId.id.toString()
        return result
    }

    @ApiImplicitParam(
            name = AUTHORIZATION,
            value = "验证身份",
            required = true,
            dataTypeClass = String::class,
            paramType = "header"
    )
    @ApiOperation(
            value = "删除文章",
            notes =
            """
                根据Id删除文章
            """
    )
    @DeleteMapping(value = ["/{id}"])
    fun deleteArticle(@PathVariable("id") id: Long): BaseBean {
        val result = BaseBean()
        articleService.deleteArticle(id)
        result.errMessage = "删除成功"
        return result
    }

    @ApiImplicitParam(
            name = AUTHORIZATION,
            value = "验证身份",
            required = true,
            dataTypeClass = String::class,
            paramType = "header"
    )
    @PostMapping(value = ["/edit"])
    fun editArticle(@RequestBody newArticle: NewArticle): BaseBean {
        val result = BaseBean()
        newArticleService.save(newArticle)
        return result
    }
}
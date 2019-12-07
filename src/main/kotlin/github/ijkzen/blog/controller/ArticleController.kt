package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.BaseBean
import github.ijkzen.blog.bean.articles.Article
import github.ijkzen.blog.bean.articles.NewArticle
import github.ijkzen.blog.bean.articles.NewArticlesBean
import github.ijkzen.blog.service.*
import github.ijkzen.blog.utils.AUTHORIZATION
import github.ijkzen.blog.utils.POST_DIR
import github.ijkzen.blog.utils.getAuthentication
import github.ijkzen.blog.utils.unAuthorized
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.io.File

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

    @Autowired
    private lateinit var gitService: GitService

    @Autowired
    private lateinit var mailService: MailService

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
        val master = developerService.searchMaster().get()
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
                processed = false
            }
        )
        return result
    }

    @ApiOperation(
        value = "应用某条文章修改记录",
        notes = """"
            需要站长权限，将其他用户对文章的修改，写到原来的文章中;
            将该用户添加到文章感谢名录，并且提交到仓库;
            并且发送邮件到该用户
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
            name = "newArticleId",
            value = "修改记录",
            required = true,
            paramType = "path"
        )
    )
    @GetMapping("/apply/{newArticleId}")
    fun applyArticle(@PathVariable newArticleId: Long): BaseBean {
        val authentication = getAuthentication()
        val master = developerService.searchMaster().get()
        return if (authentication!!.principal == master.nodeId) {
            val record = newArticleService.find(newArticleId)
            newArticleService.save(record.apply { processed = true })
            val originArticle = articleService.getArticle(record.origin!!).get()
            val changedArticle = articleService.getArticle(record.latest!!).get()
            originArticle.content = changedArticle.content

            if (originArticle.contributors == null) {
                originArticle.contributors =
                    if (master.developerName == record.developerName) null else record.developerName
            } else {
                if (master.developerName != record.developerName) {
                    originArticle.contributors += "${record.developerName},"
                }
            }
            articleService.save(originArticle)

            File("$POST_DIR/${record.fileName}").writeText(articleService.replaceOssUrl2RelativeUrl(originArticle.content!!))
            Thread {
                gitService.completeAll("update article: ${record.articleName}")
                val developer = developerService.searchDeveloperByName(record.developerName!!)
                mailService.sendMail(developer.email!!, "修改合并", "感谢您的贡献，您在 ${record.articleUrl} 的修改已被合并")
            }.start()

            BaseBean()
        } else {
            unAuthorized(BaseBean())
        }
    }

    @ApiOperation(
        value = "删除某条文章修改记录",
        notes = """"
            需要站长权限，删除文章修改记录
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
            name = "newArticleId",
            value = "修改记录",
            required = true,
            paramType = "path"
        )
    )
    @GetMapping("/cancel/{newArticleId}")
    fun cancelArticle(@PathVariable newArticleId: Long): BaseBean {
        val authentication = getAuthentication()
        val master = developerService.searchMaster().get()
        return if (authentication!!.principal == master.nodeId) {
            val newArticle = newArticleService.find(newArticleId)
            newArticleService.save(newArticle.apply { processed = true })
            val developer = developerService.searchDeveloperByName(newArticle.developerName!!)
            Thread {
                mailService.sendMail(
                    developer.email!!,
                    "文章修改未通过",
                    "非常抱歉，您对 ${newArticle.articleUrl} 的修改未通过站长的审核"
                )
            }.start()

            BaseBean()
        } else {
            unAuthorized(BaseBean())
        }
    }

    @ApiOperation(
        value = "获取文章修改列表",
        notes = """
            需要站长权限，获取未处理的文章修改记录
        """
    )
    @ApiImplicitParam(
        name = AUTHORIZATION,
        value = "验证身份",
        required = true,
        dataTypeClass = String::class,
        paramType = "header"
    )
    @GetMapping("/edit/list")
    fun getEditArticleList(): NewArticlesBean {
        val authentication = getAuthentication()
        val master = developerService.searchMaster().get()
        return if (authentication!!.principal == master.nodeId) {
            NewArticlesBean().apply { list = newArticleService.getEditList() }
        } else {
            unAuthorized(BaseBean()) as NewArticlesBean
        }
    }
}
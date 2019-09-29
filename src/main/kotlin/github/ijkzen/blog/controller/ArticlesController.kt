package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.articles.Article
import github.ijkzen.blog.bean.articles.ArticleBean
import github.ijkzen.blog.bean.articles.ArticlesBean
import github.ijkzen.blog.service.ArticleService
import github.ijkzen.blog.utils.ASC
import github.ijkzen.blog.utils.DESC
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(value = "获取文章", description = "不检查权限", tags = ["获取文章"])
@RequestMapping("/articles")
@RestController
class ArticlesController {

    @Autowired
    private lateinit var articleService: ArticleService

    @ApiOperation(
            value = "根据文章Id获取文章",
            notes =
            """
            如果文章Id不存在会返回，
                {
                errCode: "404",
                errMessage: "have no article for this id",
                article: null
                }
            """
    )
    @ApiImplicitParam(
            name = "id",
            value = "文章Id",
            required = true,
            dataTypeClass = Long::class
    )
    @GetMapping(value = ["/{id}"])
    fun getArticle(@PathVariable("id") id: Long): ArticleBean {
        val result = ArticleBean(null)
        val optionArticle = articleService.getArticle(id)
        if (!optionArticle.isPresent) {
            result.errCode = "404"
            result.errMessage = "have no article for this id"
        } else {
            result.article = optionArticle.get()
        }

        return result
    }

    @ApiOperation(
            value = "根据文章类型获取文章列表",
            notes =
            """
                如果该类型不存在，文章列表为空
            """
    )
    @ApiImplicitParam(
            name = "category",
            value = "文章分类",
            required = true,
            dataTypeClass = String::class
    )
    @GetMapping(value = ["/category/{category}"])
    fun getCategoryArticles(@PathVariable("category") category: String): ArticlesBean {
        val result = ArticlesBean(null)
        return result.apply {
            this.list = articleService.getCategoryArticles(category)
        }
    }

    @ApiOperation(
            value = "按照升序或者降序获取文章分页",
            notes =
            """
                order的值有"DESC"和"ASC"，分别对应降序和升序，以文章创建时间为标准；
                每页文章有15篇，如果总计有18篇文章，应该只有2页，第2页只有3篇文章，
                但是如果违规访问第3页，则会返回最后15篇文章
            """
    )
    @ApiImplicitParams(
            ApiImplicitParam(name = "order", value = "DESC|ASC, 标记升序降序", required = true, dataTypeClass = String::class),
            ApiImplicitParam(name = "page", value = "页数", required = true, dataTypeClass = Int::class)
    )
    @GetMapping(value = ["/list/{order}/{page}"])
    fun getArticles(@PathVariable("order") order: String, @PathVariable page: Int): ArticlesBean {
        val result = ArticlesBean(null)
        when (order.toUpperCase()) {
            DESC -> {
                result.list = getListForPage(page, articleService.getArticlesDesc())
            }
            ASC -> {
                result.list = getListForPage(page, articleService.getArticlesAsc())
            }
            else -> {
                result.list = getListForPage(page, articleService.getArticlesDesc())
            }
        }

        return result
    }

    private fun getListForPage(page: Int, list: List<Article>): List<Article> {
        return if (list.size > 15) {
            if (list.size > 15 * page) {
                list.slice(IntRange((page - 1) * 15, page * 15 - 1))
            } else {
                val start = if ((page - 1) * 15 > list.size) list.size - 16 else (page - 1) * 15
                list.slice(IntRange(start, list.size - 1))
            }
        } else {
            list
        }
    }

    @ApiOperation(
            value = "通过关键词，获取文章列表",
            notes =
            """
                关键词会在文章标题和文章内容同时搜索    
            """
    )
    @ApiImplicitParam(
            name = "keywords",
            value = "关键词",
            required = true,
            dataTypeClass = String::class
    )
    @GetMapping(value = ["/search/{keywords}"])
    fun getArticlesByKeywords(@PathVariable("keywords") keywords: String): ArticlesBean {
        val result = ArticlesBean(null)
        return result.apply {
            this.list = articleService.getArticlesByKeywords(keywords)
        }
    }
}

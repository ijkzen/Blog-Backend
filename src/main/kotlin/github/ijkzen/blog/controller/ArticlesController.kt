package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.articles.Article
import github.ijkzen.blog.bean.articles.ArticleBean
import github.ijkzen.blog.bean.articles.ArticlesBean
import github.ijkzen.blog.service.ArticleService
import github.ijkzen.blog.utils.ASC
import github.ijkzen.blog.utils.DESC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/articles")
@RestController
class ArticlesController {

    @Autowired
    private lateinit var articleService: ArticleService

    @GetMapping(value = ["/{id}"])
    fun getArticle(@PathVariable("id") id: String): ArticleBean {
        val result = ArticleBean(null)
        val optionArticle = articleService.getArticle(id.toLong())
        if (optionArticle.isEmpty) {
            result.errCode = "404"
            result.errMessage = "have no article for this id"
        } else {
            result.article = optionArticle.get()
        }

        return result
    }

    @GetMapping(value = ["/category/{category}"])
    fun getCategoryArticles(@PathVariable("category") category: String): ArticlesBean {
        val result = ArticlesBean(null)
        return result.apply {
            this.list = articleService.getCategoryArticles(category)
        }
    }

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

    @GetMapping(value = ["/search/{keywords}"])
    fun getArticlesByKeywords(@PathVariable("keywords") keywords: String): ArticlesBean {
        val result = ArticlesBean(null)
        return result.apply {
            this.list = articleService.getArticlesByKeywords(keywords)
        }
    }
}
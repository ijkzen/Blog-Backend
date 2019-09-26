package github.ijkzen.blog.service

import github.ijkzen.blog.bean.articles.Article
import github.ijkzen.blog.repository.ArticleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

/**
 * @Author ijkzen
 * @Date 2019/9/26
 */
@Service
class ArticleService {

    @Autowired
    private lateinit var developerService: DeveloperService

    @Autowired
    private lateinit var articleRepository: ArticleRepository


    private fun parseMd2Object(markdown: File) {
        val showdown = markdown.readText()
        val fileName = markdown.name
        val author = getAuthor(showdown)
        val title = getTitle(showdown, markdown)
        val category = getCategories(showdown)
        var visits: Long = 0
        var commentId: Long = 0
        val content = replaceUrl(showdown)
        var createdTime = Date()
        val updatedTime = Date()
        val `abstract` = showdown.substring(startIndex = 0, endIndex = 150)
        var isShow = true
        var id: Long? = null
        if (exist(fileName)) {
            val originArticle = articleRepository.findByFileName(fileName)
            visits = originArticle.visits ?: 0
            commentId = originArticle.commentId ?: 0
            createdTime = originArticle.createdTime ?: Date()
            isShow = originArticle.isShow ?: true
            id = originArticle.id ?: 0
        }
        articleRepository.save(
                Article(
                        fileName,
                        author,
                        isShow,
                        id,
                        title,
                        category,
                        visits,
                        commentId,
                        createdTime,
                        updatedTime,
                        content,
                        abstract
                )
        )
    }

    fun getArticles(): List<Article> = articleRepository.findAll()

    private fun getAuthor(markdown: String): String {
        val meta = getMeta(markdown)
        return if (meta == null) {
            developerService.searchMaster().developerName!!
        } else {
            val parts = meta.split("\n")
            var author: String?
            author = parts.find { it.contains("author") }?.replace("author:", "")?.trim()
            if (author == null) {
                author = developerService.searchMaster().developerName!!
            }
            author
        }
    }

    private fun getTitle(markdown: String, file: File): String {
        val meta = getMeta(markdown)
        return if (meta == null) {
            file.name.substring(file.name.lastIndexOf("-") + 1)
        } else {
            val parts = meta.split("\n")
            var title: String?
            title = parts.find { it.contains("title") }?.replace("title:", "")?.trim()
            if (title == null) {
                title = file.name.substring(file.name.lastIndexOf("-") + 1)
            }
            title
        }
    }

    private fun getCategories(markdown: String): String {
        val meta = getMeta(markdown)
        return if (meta == null) {
            ""
        } else {
            val parts = meta.split("\n")
            var categories: String?
            categories = parts.find { it.contains("categories") }?.replace("categories:", "")?.trim()
            if (categories == null) {
                categories = ""
            }
            categories
        }
    }

    private fun getMeta(markdown: String): String? {
        var result: String? = null
        val regex = "---[\\s\\S]*---"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(markdown)

        if (matcher.find()) {
            result = matcher.group(0).replace("---", "")
        }

        return result
    }

    private fun exist(fileName: String): Boolean {
        return fileName in getArticles().flatMap {
            val list: Iterable<String> = ArrayList()
            (list as ArrayList).add(it.fileName!!)
            list
        }
    }

    private fun replaceUrl(markdown: String): String {
        //todo
        return ""
    }
}
package github.ijkzen.blog.service

import github.ijkzen.blog.bean.articles.Article
import github.ijkzen.blog.bean.category.Category
import github.ijkzen.blog.repository.ArticleRepository
import github.ijkzen.blog.utils.CDN_DOMAIN
import github.ijkzen.blog.utils.POST_DIR
import org.hibernate.SessionFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.text.SimpleDateFormat
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

    @Autowired
    private lateinit var ossService: OSSService

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    private val logger = LoggerFactory.getLogger(javaClass)

    fun completeAll() {
        ossService.uploadAllImages()
        storeArticles()
    }

    fun storeArticles() {
        File(POST_DIR).listFiles()?.forEach {
            parseMd2Object(it)
        }
    }

    fun getArticle(id: Long): Optional<Article> {
        return articleRepository.findById(id)
    }

    fun getCategoryArticles(category: String): List<Article> {
        return articleRepository.findArticlesByCategoryContaining(category)
    }

    fun getArticlesDesc(): List<Article> {
        return articleRepository.findByShownTrueAndDeletedFalseOrderByCreatedTimeDesc()
    }

    fun getArticlesAsc(): List<Article> {
        return articleRepository.findByShownTrueAndDeletedFalseOrderByCreatedTimeAsc()
    }

    fun getArticlesByKeywords(keywords: String): List<Article> {
        return articleRepository.findByTitleContainingAndContentContaining(keywords, keywords)
    }

    private fun parseMd2Object(markdown: File) {
        val showdown = markdown.readText()
        val fileName = markdown.name
        val author = getAuthor(showdown)
        val title = getTitle(showdown, markdown)
        val category = getCategories(showdown)
        var visits: Long = 0
        var commentId: Long = 0
        val content = replaceUrl(showdown)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val createdTime = dateFormat.parse(fileName.substring(0, 10))
        var updatedTime: Date? = Date()
        val `abstract` = getAbstract(showdown)
        var isShow = true
        var id: Long? = null
        var isDelete: Boolean? = false
        if (exist(fileName)) {
            val originArticle = articleRepository.findByFileName(fileName)
            visits = originArticle.visits ?: 0
            commentId = originArticle.commentId ?: 0
            isShow = originArticle.shown ?: true
            id = originArticle.id ?: -1
            isDelete = originArticle.deleted ?: false

            if (content == originArticle.content) {
                updatedTime = originArticle.updatedTime
            }
        }
        articleRepository.save(
                Article(
                        id,
                        fileName,
                        author,
                        isShow,
                        isDelete,
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

    fun getAuthor(markdown: String): String {
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

    fun getTitle(markdown: String, file: File): String {
        val meta = getMeta(markdown)
        return if (meta == null) {
            file.name.substring(file.name.lastIndexOf("-") + 1).replace("\\.md", "")
        } else {
            val parts = meta.split("\n")
            var title: String?
            title = parts.find { it.contains("title") }?.replace("title:", "")?.trim()
            if (title == null) {
                title = file.name.substring(file.name.lastIndexOf("-") + 1).replace("\\.md", "")
            }
            title
        }
    }

    fun getCategories(markdown: String): String {
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

    //    ![数组图解](/assets/images/2019/09/17/strassen_first.jpg)
    fun replaceUrl(markdown: String): String {
        var tmp = markdown
        val regex = "!\\[.*?]\\(\\.\\./assets/images.*?\\)"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(markdown)
        val cdn = if (ossService.getOssInUse() == null) CDN_DOMAIN else ossService.getOssInUse()!!.cdnDomain
        while (matcher.find()) {
            val result = matcher.group(0)
            val description = result.substring(result.indexOf("["), result.indexOf("]"))
                    .replace("[", "")
                    .replace("]", "")
                    .trim()

            val url = cdn + result.substring(result.indexOf("/images"), result.length - 1)
            tmp = tmp.replace(result, "![$description]($url)")
        }
        return tmp
    }

    fun save(article: Article) = articleRepository.save(article)

    fun deleteArticle(id: Long) {
        articleRepository.deleteArticle(id)
    }

    private fun getAbstract(article: String): String {
        return if (article.length < 150) {
            article.split("---")[2]
        } else {
            val content = article.split("---")[2]
            content.substring(startIndex = 0, endIndex = if (content.length > 150) 150 else content.length - 1)
        }
    }

    fun getCategories(): List<Category> {
        val sql = " select category, count(*) as size from Article group by category"
        val session = sessionFactory.openSession()
        val query = session.createQuery(sql)
        this.logger.error(query.list().toString())
        return query.list() as List<Category>
    }
}
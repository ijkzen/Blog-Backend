package github.ijkzen.blog.service

import github.ijkzen.blog.bean.articles.Article
import github.ijkzen.blog.repository.ArticleRepository
import github.ijkzen.blog.utils.CDN_DOMAIN
import github.ijkzen.blog.utils.POST_DIR
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

    fun completeAll() {
        ossService.uploadAllImages()
        storeArticles()
    }

    private fun storeArticles() {
        File(POST_DIR).listFiles()?.forEach {
            parseMd2Object(it)
        }
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

        var createdTime = dateFormat.parse(fileName.substring(0, 10))
        var updatedTime: Date? = Date()
        val `abstract` = showdown.substring(startIndex = 0, endIndex = showdown.length / 3)
        var isShow = true
        var id: Long? = null
        if (exist(fileName)) {
            val originArticle = articleRepository.findByFileName(fileName)
            visits = originArticle.visits ?: 0
            commentId = originArticle.commentId ?: 0
            isShow = originArticle.isShow ?: true
            id = originArticle.id ?: -1

            if (content == originArticle.content) {
                updatedTime = originArticle.updatedTime
            }
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
}
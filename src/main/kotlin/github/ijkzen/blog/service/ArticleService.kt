package github.ijkzen.blog.service

import github.ijkzen.blog.bean.articles.Article
import github.ijkzen.blog.bean.category.Category
import github.ijkzen.blog.bean.oss.OSS
import github.ijkzen.blog.repository.ArticleRepository
import github.ijkzen.blog.repository.OSSRepository
import github.ijkzen.blog.service.oos.AliyunOSS
import github.ijkzen.blog.service.oos.QiNiuOSS
import github.ijkzen.blog.utils.DOMAIN
import github.ijkzen.blog.utils.POST_DIR
import org.hibernate.Session
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional
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
    private lateinit var qiNiuOss: QiNiuOSS

    @Autowired
    private lateinit var aliyunOSS: AliyunOSS

    @Autowired
    private lateinit var ossRepository: OSSRepository

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    private val logger = LoggerFactory.getLogger(javaClass)

    private var oss: OSS? = null

    private var metaPattern: Pattern? = null

    private var imagePattern: Pattern? = null

    private fun getMetaPattern(): Pattern {
        if (metaPattern == null) {
            val regex = "---[\\s\\S]*---"
            metaPattern = Pattern.compile(regex)
        }
        return metaPattern!!
    }

    private fun getImagePattern(): Pattern {
        if (imagePattern == null) {
            val regex = "!\\[.*?]\\(\\.\\./assets/images.*?\\)"
            imagePattern = Pattern.compile(regex)
        }
        return imagePattern!!
    }

    fun completeAll() {
        val list = ossRepository.findByInUseIsTrue()
        logger.info("oss size: ${list?.size}")
        if (list != null && list.isNotEmpty()) {
            oss = list[0]
            if (oss!!.category == "aliyun") {
                aliyunOSS.uploadAllImages()
            } else if (oss!!.category == "qiniu") {
                qiNiuOss.uploadAllImages()
            }
        }
        storeArticles()
    }

    private fun storeArticles() {
        val files = File(POST_DIR).listFiles()!!
        Collections.sort(files.asList(), object : Comparator<File> {
            override fun compare(p0: File?, p1: File?): Int {
                if (p0!!.isFile && p1!!.isFile) {
                    return p0.name.compareTo(p1.name)
                } else {
                    throw FileSystemException(p0, p1, "当前文件为文件夹")
                }
            }

        })
        files.forEach {
            parseMd2Object(it)
            logger.info("File ${it.name} complete")
        }
    }

    fun getArticle(id: Long): Optional<Article> {
        return articleRepository.findById(id)
    }

    fun getCategoryArticles(category: String): List<Article> {
        return articleRepository.findArticlesByShownTrueAndCategoryContainingOrderByCreatedTimeDesc(category)
    }

    fun getArticlesDesc(): List<Article> {
        return articleRepository.findByShownTrueAndDeletedFalseOrderByIdDesc()
    }

    fun getArticlesAsc(): List<Article> {
        return articleRepository.findByShownTrueAndDeletedFalseOrderByIdAsc()
    }

    fun getArticlesByKeywords(keywords: String): List<Article> {
        return articleRepository.findByTitleContainingOrContentContainingOrderByCreatedTimeDesc(keywords, keywords)
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
        var contributors: String? = ""
        val `abstract` = getAbstract(showdown)
        var isShow = true
        var id: Long? = null
        var isDelete: Boolean? = false
        if (exist(fileName)) {
            val originArticle = articleRepository.findByFileNameAndShownTrue(fileName)
            visits = originArticle.visits ?: 0
            commentId = originArticle.commentId ?: 0
            isShow = originArticle.shown ?: true
            id = originArticle.id ?: -1
            isDelete = originArticle.deleted ?: false
            contributors = originArticle.contributors

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
                contributors,
                abstract
            )
        )
    }

    fun getArticles(): List<Article> = articleRepository.findAll()

    fun getAuthor(markdown: String): String {
        val meta = getMeta(markdown)
        return if (meta == null) {
            developerService.searchMaster().get().developerName!!
        } else {
            val parts = meta.split("\n")
            var author: String?
            author = parts.find { it.contains("author") }?.replace("author:", "")?.trim()
            if (author == null) {
                author = developerService.searchMaster().get().developerName!!
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
            "undefined"
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
        val pattern = getMetaPattern()
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

    //        ![       ](../assets/images/2019/09/04/stock-pic.png)
//    ![数组图解](../assets/images/2019/09/17/strassen_first.jpg)
    fun replaceUrl(markdown: String): String {
        var tmp = markdown
        val pattern = getImagePattern()
        val matcher = pattern.matcher(markdown)
        val cdn = if (oss == null) DOMAIN else oss!!.cdnDomain
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
            if (article.split("---").size < 3) "" else article.split("---")[2]
        } else {
            val content = article.split("---")[2]
            content.substring(startIndex = 0, endIndex = if (content.length > 150) 150 else content.length - 1)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Transactional
    fun getCategories(): List<Category> {
        val list = LinkedList<Category>()
        val sql = " select category, count(*) as size from Article where shown=1 group by category"
        val session = entityManager.unwrap(Session::class.java)
        val result: List<Array<Any>> = session.createNativeQuery(sql).resultList as List<Array<Any>>
        result.forEach {
            val category = Category()
            category.category = it[0] as String
            category.size = (it[1] as BigInteger).toLong()
            list.add(category)
        }
        session.close()
        println(list.toString())
        return list
    }

    fun getArticleMaxId(): Long? {
        val sql = "select id from Article order by id desc limit 1"
        val session = entityManager.unwrap(Session::class.java)
        val result: List<Any?> = session.createNativeQuery(sql).resultList as List<Any?>
        return if (result.isEmpty()) {
            null
        } else {
            (result[0] as BigInteger).toLong()
        }
    }

    //  https://cdn.nextto.top/images/2019/09/04/stock-pic.png
    fun replaceOssUrl2RelativeUrl(content: String): String {
        val list = ossRepository.findAll()
        var result = ""
        list.forEach {
            result = content.replace(it.cdnDomain!!, "../assets")
        }
        return result
    }

    fun getPreviousArticle(current: Long): Article? {
        return getPrevious(current)
    }

    private fun getPrevious(current: Long): Article? {
        if (current == 1.toLong()) {
            return null
        }
        val article = articleRepository.findById(current - 1).get()
        return if (article.shown!! && !article.deleted!!) {
            article
        } else {
            getPrevious(current - 1)
        }
    }

    fun getNextArticle(current: Long): Article? {
        val max = getArticleMaxId()
        return getNext(current, max)
    }

    fun getNext(current: Long, max: Long?): Article? {
        if (null == max || max <= current) {
            return null
        }
        val article = articleRepository.findById(current + 1).get()
        return if (article.shown!! && !article.deleted!!) {
            article
        } else {
            getNext(current + 1, max)
        }
    }

}
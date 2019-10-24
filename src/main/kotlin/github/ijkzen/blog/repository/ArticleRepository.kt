package github.ijkzen.blog.repository

import github.ijkzen.blog.bean.articles.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

/**
 * @Author ijkzen
 * @Date 2019/9/26
 */
@Repository
@Transactional
interface ArticleRepository : JpaRepository<Article, Long> {

    fun findByFileName(fileName: String): Article

    fun findArticlesByShownTrueAndCategoryContainingOrderByCreatedTimeDesc(category: String): List<Article>

    fun findByShownTrueAndDeletedFalseOrderByCreatedTimeDesc(): List<Article>

    fun findByShownTrueAndDeletedFalseOrderByCreatedTimeAsc(): List<Article>

    fun findByTitleContainingOrContentContainingOrderByCreatedTimeDesc(keywords: String, contents: String): List<Article>

    @Modifying
    @Query("update Article set deleted=1 where id=?1")
    fun deleteArticle(id: Long)
}
package github.ijkzen.blog.repository

import github.ijkzen.blog.bean.articles.Article
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @Author ijkzen
 * @Date 2019/9/26
 */
interface ArticleRepository : JpaRepository<Article, Long> {

    fun findByFileName(fileName: String): Article

    fun findArticlesByCategoryContaining(category: String): List<Article>

    fun findByIsShowTrueAndIsDeleteFalseOrderByCreatedTimeDesc(): List<Article>

    fun findByIsShowTrueAndIsDeleteFalseOrderByCreatedTimeAsc(): List<Article>

    fun findByTitleContainingAndContentContaining(keywords: String, contents: String): List<Article>
}
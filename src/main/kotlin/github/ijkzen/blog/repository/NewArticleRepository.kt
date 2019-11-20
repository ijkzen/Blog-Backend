package github.ijkzen.blog.repository

import github.ijkzen.blog.bean.articles.NewArticle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @Author ijkzen
 * @Date 2019/9/29
 */
@Repository
interface NewArticleRepository : JpaRepository<NewArticle, Long> {

    fun findByProcessedFalse(): List<NewArticle>
}
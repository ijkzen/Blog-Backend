package github.ijkzen.blog.repository

import github.ijkzen.blog.bean.comment.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

/**
 * @Author ijkzen
 * @Date 2019/9/30
 */
@Repository
@Transactional
interface CommentRepository : JpaRepository<Comment, Long> {

    fun findCommentsByArticleIdAndDeletedFalse(articleId: Long): List<Comment>

    fun findCommentsByReplyIdAndDeletedFalse(parent: Long): List<Comment>

    @Modifying
    @Query("update Comment set deleted=1 where id = ?1")
    fun deleteComment(commentId: Long)

    @Modifying
    @Query("update Comment set reported=1 where id= ?1")
    fun reportComment(id: Long)

    fun findCommentsByReportedTrue(): List<Comment>

    @Modifying
    @Query("update Comment set reported=0 where id= ?1")
    fun cancelComment(id: Long)
}
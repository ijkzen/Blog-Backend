package github.ijkzen.blog.service

import github.ijkzen.blog.bean.comment.Comment
import github.ijkzen.blog.repository.CommentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @Author ijkzen
 * @Date 2019/9/30
 */
@Service
class CommentService {

    @Autowired
    private lateinit var commentRepository: CommentRepository

    fun addComment(comment: Comment) {
        comment.id = null
        comment.deleted = false
        comment.reported = false
        commentRepository.save(comment)
    }

    fun deleteComment(id: Long) {
        commentRepository.deleteComment(id)
        commentRepository.findCommentsByReplyIdAndDeletedFalse(id)
            .forEach {
                deleteComment(it.id!!)
            }
    }

    fun findComments(articleId: Long): List<Comment> {
        return commentRepository.findCommentsByArticleIdAndDeletedFalse(articleId)
    }

    fun reportComment(id: Long) {
        commentRepository.reportComment(id)
    }

    fun findReportComments(): List<Comment> {
        return commentRepository.findCommentsByReportedTrue()
    }

    fun findCommentById(id: Long): Comment {
        return commentRepository.findById(id).get()
    }

    fun getCount(): Long {
        return commentRepository.count()
    }

    fun cancelReport(id: Long) {
        commentRepository.cancelComment(id)
    }
}
package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.BaseBean
import github.ijkzen.blog.bean.comment.Comment
import github.ijkzen.blog.bean.comment.CommentsBean
import github.ijkzen.blog.service.CommentService
import github.ijkzen.blog.service.DeveloperService
import github.ijkzen.blog.service.MailService
import github.ijkzen.blog.utils.AUTHORIZATION
import github.ijkzen.blog.utils.getAuthentication
import github.ijkzen.blog.utils.unAuthorized
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * @Author ijkzen
 * @Date 2019/9/30
 */
@Api(
        value = "评论接口",
        tags = ["评论相关"],
        description = "不需要检查权限"
)
@RestController
@RequestMapping(value = ["/comment"])
class CommentController {

    @Autowired
    private lateinit var commentService: CommentService

    @Autowired
    private lateinit var developerService: DeveloperService

    @Autowired
    private lateinit var mailService: MailService

    @ApiOperation(
            value = "根据文章Id获取评论",
            notes =
            """
                只会返回没有被删掉的评论;
                不存在的文章Id或者没有评论的文章会返回空数组;
            """
    )
    @GetMapping(value = ["/{articleId}"])
    fun getComments(@PathVariable articleId: Long): CommentsBean {
        val result = CommentsBean()
        result.list = commentService.findComments(articleId).sortedByDescending { it.createdTime }
        return result
    }

    @ApiOperation(
            value = "添加评论",
            notes =
            """
                id字段填写与否并不重要，后台会擦除入库                        
            """
    )
    @ApiImplicitParam(
            name = "comment",
            value = "评论实体",
            required = true,
            dataTypeClass = Comment::class,
            dataType = "Comment"
    )
    @PostMapping(value = ["/new"])
    fun addComment(@RequestBody comment: Comment): BaseBean {
        commentService.addComment(comment)
        if (comment.replyId != null) {
            val receiverId = commentService.findCommentById(comment.replyId!!).authorId
            val receiver = developerService.searchDeveloperById(receiverId)
            mailService.sendMail(receiver.email!!, "新回复", "快来博客 ${comment.articleUrl} 看看，你有新的回复了")
        }
        val master = developerService.searchMaster()
        mailService.sendMail(master.email!!, "新回复", "快来博客 ${comment.articleUrl} 看看，你有新的回复了")
        return BaseBean()
    }

    @ApiOperation(
            value = "删除评论",
            notes =
            """
                级联删除当前评论下的所有评论，此API需要验证站长权限
            """
    )
    @ApiImplicitParams(
            ApiImplicitParam(
                    name = AUTHORIZATION,
                    value = "验证身份",
                    required = true,
                    dataTypeClass = String::class,
                    paramType = "header"
            ),
            ApiImplicitParam(
                    name = "id",
                    value = "被删除的评论id",
                    required = true,
                    dataTypeClass = Long::class
            )
    )
    @DeleteMapping(value = ["/{id}"])
    fun deleteComment(@PathVariable id: Long): BaseBean {
        val result = BaseBean()
        val authentication = getAuthentication()
        return if (authentication == null) {
            unAuthorized(result)
        } else {
            val master = developerService.searchMaster()
            if (master.nodeId == authentication.principal) {
                commentService.deleteComment(id)
                result.apply {
                    errMessage = "评论删除成功"
                }
            } else {
                unAuthorized(result)
            }
        }
    }

    @ApiOperation(
            value = "举报评论",
            notes =
            """
                此处不需要权限，可能会被滥用，以后可能会添加权限    
            """
    )
    @ApiImplicitParam(
            name = "id",
            value = "举报的评论Id",
            required = true,
            dataTypeClass = Long::class

    )
    @GetMapping(value = ["/report/{id}"])
    fun reportComment(@PathVariable id: Long): BaseBean {
        val result = BaseBean()
        commentService.reportComment(id)
        val master = developerService.searchMaster()
        mailService.sendMail(master.email!!, "举报评论", "有新的举报评论")
        return result
    }

    @ApiOperation(
            value = "获取举报评论列表",
            notes =
            """
                资源比较敏感，所以只有站长可以获取
            """
    )
    @ApiImplicitParam(
            name = AUTHORIZATION,
            value = "验证身份",
            required = true,
            dataTypeClass = String::class,
            paramType = "header"
    )
    @GetMapping(value = ["/report/list"])
    fun getReportComments(): CommentsBean {
        val result = CommentsBean()
        val authentication = getAuthentication()

        return if (authentication == null) {
            unAuthorized(result) as CommentsBean
        } else {
            val master = developerService.searchMaster()
            if (master.nodeId == authentication.principal) {
                result.list = commentService.findReportComments()
                result
            } else {
                unAuthorized(result) as CommentsBean
            }
        }
    }
}
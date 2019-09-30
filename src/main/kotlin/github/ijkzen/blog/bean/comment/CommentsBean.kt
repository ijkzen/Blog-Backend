package github.ijkzen.blog.bean.comment

import github.ijkzen.blog.bean.BaseBean

/**
 * @Author ijkzen
 * @Date 2019/9/30
 */
data class CommentsBean(
        var list: List<Comment>? = null
) : BaseBean()
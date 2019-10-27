package github.ijkzen.blog.bean.xiaoice

import github.ijkzen.blog.bean.BaseBean

/**
 * @Author ijkzen
 * @Date 2019/10/27
 */
data class MessagesBean(
        var list: List<ChatMessage>? = null
) : BaseBean()
package github.ijkzen.blog.bean.xiaoice

/**
 * @Author ijkzen
 * @Date 2019/10/26
 */
data class ChatMessagesBean(
        var total: Long? = null,
        var ok: Int? = 0,
        var msg: String? = null,
        var maxPage: Int = 0,
        var data: List<ChatMessage>
)
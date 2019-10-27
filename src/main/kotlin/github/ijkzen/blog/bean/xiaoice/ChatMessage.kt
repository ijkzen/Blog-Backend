package github.ijkzen.blog.bean.xiaoice

/**
 * @Author ijkzen
 * @Date 2019/10/26
 */
data class ChatMessage(
        var id: Long? = null,
        var created_at: String? = null,
        var sender_id: Long? = null,
        var recipient_id: Long? = null,
        var text: String? = null,
        var attachment: List<Attachment>? = null
)
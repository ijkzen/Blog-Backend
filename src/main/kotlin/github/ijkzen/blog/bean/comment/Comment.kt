package github.ijkzen.blog.bean.comment

import java.util.*
import javax.persistence.*

/**
 * @Author ijkzen
 * @Date 2019/9/30
 */
@Entity
data class Comment(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long?,

        @Column
        var authorId: Long,

        @Column
        var articleId: Long,

        @Column(columnDefinition = "bigint default 0")
        var parent: Long,

        @Column(columnDefinition = "timestamp default CURRENT_TIMESTAMP")
        var createdTime: Date,

        @Column
        var content: String,

        @Column(columnDefinition = "bit default 0")
        var reported: Boolean,

        @Column(columnDefinition = "bit default 0")
        var deleted: Boolean
)
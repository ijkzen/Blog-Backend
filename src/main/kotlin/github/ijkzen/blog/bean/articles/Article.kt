package github.ijkzen.blog.bean.articles

import java.util.*
import javax.persistence.*

/**
 * @Author ijkzen
 * @Date 2019/9/25
 */

@Entity
data class Article(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long?,

        @Column
        var fileName: String?,

        @Column
        var author: String?,

        @Column(columnDefinition = "bit default 1")
        var shown: Boolean?,

        @Column(columnDefinition = "bit default 0")
        var deleted: Boolean?,

        @Column
        var title: String?,

        @Column
        var category: String?,

        @Column
        var visits: Long?,

        @Column
        var commentId: Long?,

        @Column(columnDefinition = "timestamp default CURRENT_TIMESTAMP")
        @Temporal(TemporalType.TIMESTAMP)
        var createdTime: Date?,

        @Column(columnDefinition = "timestamp default CURRENT_TIMESTAMP")
        @Temporal(TemporalType.TIMESTAMP)
        var updatedTime: Date?,

        @Column(columnDefinition = "text not null")
        var content: String?,

        @Column(columnDefinition = "text not null")
        var `abstract`: String?
)
package github.ijkzen.blog.bean.articles

import java.util.*
import javax.persistence.*

/**
 * @Author ijkzen
 * @Date 2019/9/25
 */
@Entity
data class Article(
        @Column
        var fileName: String?,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long?,

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
) {
    constructor() : this(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
    )
}
package github.ijkzen.blog.bean.articles

import javax.persistence.*

/**
 * @Author ijkzen
 * @Date 2019/9/29
 */

@Entity
data class NewArticle(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long?,
        @Column(nullable = false)
        var developerName: String?,
        @Column(nullable = false)
        var origin: Long?,
        @Column(nullable = false)
        var latest: Long?
)
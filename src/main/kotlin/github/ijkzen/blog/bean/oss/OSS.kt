package github.ijkzen.blog.bean.oss

import javax.persistence.*

/**
 * @Author ijkzen
 * @Date 2019/9/26
 */
@Entity
data class OSS(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int,

        @Column
        var category: String?,

        @Column
        var accessKey: String?,

        @Column
        var secretKey: String?,

        @Column
        var bucket: String?,

        @Column
        var cdnDomain: String?,

        @Column(columnDefinition = "boolean default 0")
        var inUse: Boolean = false
)
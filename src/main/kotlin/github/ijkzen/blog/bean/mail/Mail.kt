package github.ijkzen.blog.bean.mail

import github.ijkzen.blog.utils.EMPTY
import javax.persistence.*

@Entity
data class Mail(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @Column
        var host: String = EMPTY,

        @Column
        var userName: String = EMPTY,

        @Column
        var password: String = EMPTY,

        @Column
        var port: Int,

        @Column
        var startTls: Boolean,

        @Column
        var inUse: Boolean
)
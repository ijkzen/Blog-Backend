package github.ijkzen.blog.bean.record

import github.ijkzen.blog.utils.EMPTY
import java.util.*
import javax.persistence.*

@Entity
data class RequestRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column
    var operatingSystem: String,

    @Column
    var operatingSystemVersion: String,

    @Column
    var browser: String,

    @Column
    var browserVersion: String,

    @Column
    var device: String,

    @Column(columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    var time: Date,

    @Column
    var ip: String,

    @Column
    var url: String,

    @Column
    var country: String? = EMPTY,

    @Column
    var region: String? = EMPTY,

    @Column
    var city: String? = EMPTY,

    @Column
    var httpMethod: String = EMPTY
)
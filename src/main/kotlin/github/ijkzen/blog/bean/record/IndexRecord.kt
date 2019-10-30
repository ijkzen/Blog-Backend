package github.ijkzen.blog.bean.record

import javax.persistence.*

/**
 * @Author ijkzen
 * @Date 2019/10/30
 */
@Entity
data class IndexRecord(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column
    var count: Long = 1
)
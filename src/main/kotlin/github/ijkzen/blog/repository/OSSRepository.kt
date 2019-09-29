package github.ijkzen.blog.repository

import github.ijkzen.blog.bean.oss.OSS
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @Author ijkzen
 * @Date 2019/9/26
 */
@Repository
interface OSSRepository : JpaRepository<OSS, Int> {

    fun save(oss: OSS)

    fun findByInUseIsTrue(): List<OSS>?

}
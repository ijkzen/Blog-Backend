package github.ijkzen.blog.repository

import github.ijkzen.blog.bean.oss.OSS
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @Author ijkzen
 * @Date 2019/9/26
 */

interface OSSRepository : JpaRepository<OSS, Int> {

    fun save(oss: OSS)

    fun findByInUseIsTrue(): List<OSS>?

}
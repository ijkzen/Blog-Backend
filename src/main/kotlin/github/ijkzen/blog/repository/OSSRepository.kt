package github.ijkzen.blog.repository

import github.ijkzen.blog.bean.oss.OSS
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

/**
 * @Author ijkzen
 * @Date 2019/9/26
 */
@Transactional
@Repository
interface OSSRepository : JpaRepository<OSS, Int> {

    fun save(oss: OSS)

    fun findByInUseIsTrue(): List<OSS>?

    @Modifying
    @Query("update OSS  set inUse=0 where inUse=1 ")
    fun deleteUseless()
}
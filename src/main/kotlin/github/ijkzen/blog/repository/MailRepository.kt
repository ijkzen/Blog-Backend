package github.ijkzen.blog.repository

import github.ijkzen.blog.bean.mail.MailConfigurationBean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
@Transactional
interface MailRepository : JpaRepository<MailConfigurationBean, Long> {

    @Modifying
    @Query("update Mail set inUse=0 where inUse=1")
    fun deleteUseless()

    fun findMailByInUseTrue(): MailConfigurationBean?
}
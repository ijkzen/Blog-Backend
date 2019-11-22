package github.ijkzen.blog.repository

import github.ijkzen.blog.bean.github.response.RepositoryBean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface Repository : JpaRepository<RepositoryBean, Long> {

    fun findByFullName(fullName: String): RepositoryBean?

    fun existsByFullName(fullName: String): Boolean

    fun deleteByFullName(fullName: String)

    fun findByState(state: String): Optional<RepositoryBean>
}
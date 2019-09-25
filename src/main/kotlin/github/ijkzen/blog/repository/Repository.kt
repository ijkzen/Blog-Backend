package github.ijkzen.blog.repository

import github.ijkzen.blog.bean.github.response.RepositoryBean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface Repository : JpaRepository<RepositoryBean, Long> {

    fun findByFullName(fullName: String): RepositoryBean?

}
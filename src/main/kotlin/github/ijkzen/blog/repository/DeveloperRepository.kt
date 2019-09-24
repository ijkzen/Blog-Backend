package github.ijkzen.blog.repository

import github.ijkzen.blog.bean.github.response.Developer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface DeveloperRepository : JpaRepository<Developer, Long> {
}
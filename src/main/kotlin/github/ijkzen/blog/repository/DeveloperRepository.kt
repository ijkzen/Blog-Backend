package github.ijkzen.blog.repository

import github.ijkzen.blog.bean.github.response.DeveloperBean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface DeveloperRepository : JpaRepository<DeveloperBean, Long> {

    fun findDeveloperBeanByNodeId(nodeId: String): DeveloperBean?

    fun findDeveloperBeanByDeveloperName(name: String): DeveloperBean?

    fun findDeveloperBeanByState(state: String): DeveloperBean?
}
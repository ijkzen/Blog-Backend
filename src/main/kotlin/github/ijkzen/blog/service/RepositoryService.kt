package github.ijkzen.blog.service

import github.ijkzen.blog.bean.github.response.RepositoryBean
import github.ijkzen.blog.repository.Repository
import github.ijkzen.blog.utils.REPOSITORY_NAME
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RepositoryService {

    @Autowired
    private lateinit var repository: Repository

    @Autowired
    private lateinit var developerService: DeveloperService

    fun save(repositoryBean: RepositoryBean) {
        repository.save(repositoryBean)
    }

    fun searchArticleRepository(): String {
        val developerName = developerService.searchMaster().developerName
        val result = repository.findByFullName("$developerName/$REPOSITORY_NAME")
        return result?.htmlUrl ?: ""
    }
}
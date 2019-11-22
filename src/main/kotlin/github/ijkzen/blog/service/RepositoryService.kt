package github.ijkzen.blog.service

import github.ijkzen.blog.bean.github.response.RepositoryBean
import github.ijkzen.blog.repository.Repository
import github.ijkzen.blog.utils.REPOSITORY_ID
import github.ijkzen.blog.utils.REPOSITORY_NAME
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import javax.transaction.Transactional

@Transactional
@Service
class RepositoryService {

    @Autowired
    private lateinit var repository: Repository

    @Autowired
    private lateinit var developerService: DeveloperService

    fun save(repositoryBean: RepositoryBean) {
        repository.save(repositoryBean)
    }

    fun getRepositoryUrl(): String {
        val developerName = developerService.searchMaster().get().developerName
        val result = repository.findByFullName("$developerName/$REPOSITORY_NAME")
        return result?.htmlUrl ?: ""
    }

    fun updateArticleRepository(repositoryBean: RepositoryBean) {
        if (repository.existsByFullName(repositoryBean.fullName!!)) {
            repository.deleteByFullName(repositoryBean.fullName!!)
        }
        save(repositoryBean)
    }

    fun findAllRepos(): Array<RepositoryBean> = repository.findAll().toTypedArray()

    fun findArticleRepo(): RepositoryBean {
        return repository.findById(File(REPOSITORY_ID).readText().toLong()).get()
    }
}
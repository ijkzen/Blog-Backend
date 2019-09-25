package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.github.request.RepositoryEntity
import github.ijkzen.blog.bean.github.response.DeveloperBean
import github.ijkzen.blog.bean.github.response.GithubEmailBean
import github.ijkzen.blog.bean.github.response.GithubTokenBean
import github.ijkzen.blog.bean.github.response.RepositoryBean
import github.ijkzen.blog.service.DeveloperService
import github.ijkzen.blog.service.GitService
import github.ijkzen.blog.service.RepositoryService
import github.ijkzen.blog.utils.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.io.File

val restTemplate = RestTemplate()

@RestController
class OAuthController {
    @Autowired
    private lateinit var developerService: DeveloperService

    @Autowired
    private lateinit var repositoryService: RepositoryService

    @Autowired
    private lateinit var gitService: GitService

    @GetMapping(value = ["/oauth/github"])
    fun getToken(@RequestParam("code") code: String) {
        val token = restTemplate.getForObject(
                "https://github.com/login/oauth/access_token?" +
                        "client_id=$CLIENT_ID" +
                        "&client_secret=$CLIENT_SECRET" +
                        "&code=$code",
                GithubTokenBean::class.java
        )
        getDeveloperInfo(token!!.accessToken)
    }

    private fun getDeveloperInfo(token: String) {

        val entity = HttpEntity("", getGithubHeaders(token))
        val result = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                entity,
                DeveloperBean::class.java
        )

        val developer = result.body!!
        developer.token = token
        File(MASTER_ID).writeText(developer.developerId.toString())
        if (developer.email.isNullOrEmpty()) {
            getDeveloperEmail(developer)
        } else {
            developerService.save(developer)
            createBlogRepository()
        }
    }

    private fun getDeveloperEmail(developerBean: DeveloperBean) {

        val entity = HttpEntity("", getGithubHeaders(developerBean.token!!))
        val email = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                entity,
                Array<GithubEmailBean>::class.java
        )
        developerBean.email = email.body!!.find { it.primary }!!.email
        developerService.save(developerBean)
        createBlogRepository()
    }

    private fun createBlogRepository() {
        if (isExistRepository()) {
            val repos = getRepos()
            repositoryService.updateArticleRepository(repos.find { it.name == REPOSITORY_NAME }!!)
        } else {
            val developer = developerService.searchMaster()
            val repository = RepositoryEntity(REPOSITORY_NAME)
            val entity = HttpEntity(repository, getGithubHeaders(developer.token!!))
            val repositoryBean = restTemplate.postForObject(
                    "https://api.github.com//user/repos",
                    entity,
                    RepositoryBean::class.java
            )
            repositoryService.updateArticleRepository(repositoryBean!!)
        }
        if (File(REPOSITORY_NAME).exists()) {
            gitService.pullAll()
        } else {
            gitService.cloneRepository()
            gitService.init()
        }
    }

    private fun isExistRepository(): Boolean {
        val repos = getRepos()
        return if (repos.isEmpty()) false else repos.any { it.name == REPOSITORY_NAME }
    }

    private fun getRepos(): Array<RepositoryBean> {
        val developer = developerService.searchMaster()
        val entity = HttpEntity("", getGithubHeaders(developer.token!!))
        return restTemplate.exchange(
                "https://api.github.com/user/repos",
                HttpMethod.GET,
                entity,
                Array<RepositoryBean>::class.java
        ).body!!
    }


}
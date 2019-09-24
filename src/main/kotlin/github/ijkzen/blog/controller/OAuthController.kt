package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.github.request.RepositoryResponse
import github.ijkzen.blog.bean.github.response.Developer
import github.ijkzen.blog.bean.github.response.GithubEmail
import github.ijkzen.blog.bean.github.response.GithubToken
import github.ijkzen.blog.service.DeveloperService
import github.ijkzen.blog.utils.CLIENT_ID
import github.ijkzen.blog.utils.CLIENT_SECRET
import github.ijkzen.blog.utils.MASTER_ID
import github.ijkzen.blog.utils.REPOSITORY_NAME
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.io.File
import java.util.*

val restTemplate = RestTemplate()

@RestController
class OAuthController {
    @Autowired
    private lateinit var developerService: DeveloperService

    @GetMapping(value = ["/oauth/github"])
    fun getToken(@RequestParam("code") code: String) {
        val token = restTemplate.getForObject(
                "https://github.com/login/oauth/access_token?" +
                        "client_id=$CLIENT_ID" +
                        "&client_secret=$CLIENT_SECRET" +
                        "&code=$code",
                GithubToken::class.java
        )

        getDeveloperInfo(token!!.accessToken)
    }

    private fun getDeveloperInfo(token: String) {

        val entity = HttpEntity("", getGithubHeaders(token))
        val result = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                entity,
                Developer::class.java
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

    private fun getDeveloperEmail(developer: Developer) {

        val entity = HttpEntity("", getGithubHeaders(developer.token!!))
        val email = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                entity,
                Array<GithubEmail>::class.java
        )

        developer.email = email.body!!.find { it.primary }!!.email
        developerService.save(developer)
        createBlogRepository()
    }

    private fun getGithubHeaders(token: String): HttpHeaders {
        val headers = HttpHeaders()
        return headers.apply {
            accept = Collections.singletonList(MediaType.APPLICATION_JSON)
            set("Authorization", "token $token")
        }
    }

    private fun createBlogRepository() {
        if (isExistRepository()) {
            System.err.println("articles repository exist")
        } else {
            val developer = developerService.searchMaster()
            val repository = RepositoryResponse(REPOSITORY_NAME)
            val entity = HttpEntity(repository, getGithubHeaders(developer.token!!))
            val rsp = restTemplate.postForObject(
                    "https://api.github.com//user/repos",
                    entity,
                    String::class.java
            )
            System.err.println(rsp)
        }
    }

    private fun isExistRepository(): Boolean {
        val developer = developerService.searchMaster()
        val entity = HttpEntity("", getGithubHeaders(developer.token!!))
        val repositories = restTemplate.exchange(
                "https://api.github.com/user/repos",
                HttpMethod.GET,
                entity,
                Array<RepositoryResponse>::class.java
        )
        return if (repositories.body!!.isEmpty()) false else repositories.body!!.any { it.name == REPOSITORY_NAME }
    }

}
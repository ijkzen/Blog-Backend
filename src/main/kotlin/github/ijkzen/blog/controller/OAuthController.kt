package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.github.request.RepositoryEntity
import github.ijkzen.blog.bean.github.request.WebHook
import github.ijkzen.blog.bean.github.request.WebHookConfig
import github.ijkzen.blog.bean.github.response.DeveloperBean
import github.ijkzen.blog.bean.github.response.GithubEmailBean
import github.ijkzen.blog.bean.github.response.GithubTokenBean
import github.ijkzen.blog.bean.github.response.RepositoryBean
import github.ijkzen.blog.service.DeveloperService
import github.ijkzen.blog.service.RepositoryService
import github.ijkzen.blog.utils.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import javax.servlet.http.HttpServletResponse

@Api(value = "授权接口", description = "授权接口", tags = ["Github授权"])
@RestController
class OAuthController {
    @Autowired
    private lateinit var developerService: DeveloperService

    @Autowired
    private lateinit var repositoryService: RepositoryService

    private val restTemplate = RestTemplate()

    private val logger = LoggerFactory.getLogger(javaClass)

    @ApiOperation(
        value = "Github授权回调URL",
        notes =
        """
                授权成功后，Github会自动访问；
                成功回调后，会获取开发者信息，并且创建仓库
            """
    )
    @ApiImplicitParam(name = "code", value = "用来向GitHub申请令牌", required = true, dataType = "String")
    @GetMapping(value = ["/oauth/github"])
    fun getToken(@RequestParam("code") code: String, response: HttpServletResponse) {
        val token = restTemplate.getForObject(
            "https://github.com/login/oauth/access_token?" +
                    "client_id=$CLIENT_ID" +
                    "&client_secret=$CLIENT_SECRET" +
                    "&code=$code",
            GithubTokenBean::class.java
        )

        response.sendRedirect("$FRONT/?nodeId=${getDeveloperInfo(token!!.accessToken)}")
    }

    private fun getDeveloperInfo(token: String): String {
        logger.error("token: $token")
        val entity = HttpEntity("", getGithubHeaders(token))
        val result = restTemplate.exchange(
            "https://api.github.com/user",
            HttpMethod.GET,
            entity,
            DeveloperBean::class.java
        )

        val developer = result.body!!
        developer.token = token
        val masterExists = developerService.masterExists()
        if (!masterExists) {
            developer.state = MASTER
        } else {
            val master = developerService.searchMaster()
            if (developer.nodeId == master.get().nodeId) {
                developer.state = MASTER
            }
        }

        developer.bio = developer.avatarUrl
        developer.avatarUrl = """$DOMAIN/developer/avatar/${developer.developerId}"""

        if (developer.email.isNullOrEmpty()) {
            getDeveloperEmail(developer)
        } else {
            developerService.save(developer)
        }

        if (!masterExists) {
            Thread {
                createBlogRepository()
            }.start()
        }
        return developer.nodeId!!
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
    }

    private fun createBlogRepository() {
        if (isExistRepository()) {
            val repos = getRepos()
            val repo = repos.find { it.name == REPOSITORY_NAME }!!
            repo.state = MASTER
            repositoryService.updateArticleRepository(repo)
        } else {
            val developer = developerService.searchMaster().get()
            val repository = RepositoryEntity(REPOSITORY_NAME)
            val entity = HttpEntity(repository, getGithubHeaders(developer.token!!))
            val repositoryBean = restTemplate.postForObject(
                "https://api.github.com//user/repos",
                entity,
                RepositoryBean::class.java
            )
            repositoryBean!!.state = MASTER
            repositoryService.updateArticleRepository(repositoryBean!!)
        }
        setWebHook()
    }

    fun isExistRepository(): Boolean {
        val repos = getRepos()
        logger.error("start print")
        repos.forEach {
            logger.error(it.name)
        }
        return if (repos.isEmpty()) false else repos.any { it.name == REPOSITORY_NAME }
    }

    private fun getRepos(): Array<RepositoryBean> {
        val developer = developerService.searchMaster().get()
        val entity = HttpEntity("", getGithubHeaders(developer.token!!))
        return restTemplate.exchange(
            "https://api.github.com/user/repos",
            HttpMethod.GET,
            entity,
            Array<RepositoryBean>::class.java
        ).body!!
    }

    fun setWebHook() {
        if (!webHookExists()) {
            val master = developerService.searchMaster().get()
            val hook = WebHook()
            hook.name = "web"
            hook.events = listOf("push")
            hook.active = true
            hook.config = WebHookConfig().apply {
                url = "$DOMAIN/articles/update"
                contentType = "json"
                insecureSsl = "0"
            }
            val entity = HttpEntity(hook, getGithubHeaders(master.token!!))
            val result = restTemplate.exchange(
                "https://api.github.com/repos/${master.developerName}/$REPOSITORY_NAME/hooks",
                HttpMethod.POST,
                entity,
                String::class.java
            )
            logger.debug(result.body)
        }
    }

    fun webHookExists(): Boolean {
        val master = developerService.searchMaster().get()
        val entity = HttpEntity("", getGithubHeaders(master.token!!))
        val result = restTemplate.exchange(
            "https://api.github.com/repos/${master.developerName}/$REPOSITORY_NAME/hooks",
            HttpMethod.GET,
            entity,
            List::class.java
        )
        return result.body!!.isNotEmpty()
    }
}
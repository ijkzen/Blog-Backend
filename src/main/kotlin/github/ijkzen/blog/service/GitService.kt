package github.ijkzen.blog.service

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import github.ijkzen.blog.utils.*
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.util.FS
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author ijkzen
 * @Date 2019/9/25
 */
@Service
class GitService {

    private val logger = LoggerFactory.getLogger(javaClass)

    val restTemplate = RestTemplate()

    @Autowired
    private lateinit var articleService: ArticleService

    companion object {
        private var git: Git? = null
            get() {
                if (field == null) {
                    field = Git.open(File(REPOSITORY_NAME))
                }
                return field
            }

        private fun getSshSessionFactory(): JschConfigSessionFactory {

            return object : JschConfigSessionFactory() {

                override fun configure(hc: OpenSshConfig.Host?, session: Session?) {
                    val config = Properties()
                    config["StrictHostKeyChecking"] = "no"
                    session?.setConfig(config)
                }

                override fun createDefaultJSch(fs: FS?): JSch {
                    val jsch = super.createDefaultJSch(fs)
                    jsch.addIdentity(".ssh/id_rsa")
                    return jsch
                }
            }
        }
    }

    @Autowired
    private lateinit var repositoryService: RepositoryService

    @Autowired
    private lateinit var developerService: DeveloperService

    fun cloneRepository() {
        val fullName = repositoryService.findArticleRepo().fullName
        if (File(".ssh/id_rsa").exists()) {
            Git.cloneRepository()
                .setURI("git@github.com:$fullName.git")
                .setDirectory(File(REPOSITORY_NAME))
                .setTransportConfigCallback {
                    (it as SshTransport).sshSessionFactory = getSshSessionFactory()
                }
                .call()
        }
    }

    fun deleteRemoteRepository(): Boolean {
        val fullName = repositoryService.findArticleRepo().fullName
        val token = developerService.searchMaster().get().token
        val entity = HttpEntity("", getGithubHeaders(token!!))
        val result = restTemplate.exchange(
            "https://api.github.com/repos/$fullName",
            HttpMethod.DELETE,
            entity,
            String::class.java
        )
        return 204 == result.statusCodeValue
    }

    fun addAll() {
        if (isAllowed()) {
            git!!.add().addFilepattern(".").call()
        }
    }

    fun commitAll(message: String) {
        val developer = developerService.searchMaster().get()
        if (isAllowed()) {
            git!!.commit()
                .setAuthor(developer.developerName, developer.email)
                .setMessage(message).call()
        }
    }

    fun pushAll() {
        if (isAllowed()) {
            logger.info("pushed")
            git!!.push().setTransportConfigCallback {
                (it as SshTransport).sshSessionFactory = getSshSessionFactory()
            }.setPushAll().call()
        }
    }

    fun init() {
        if (File(".ssh/id_rsa").exists() && !File(REPOSITORY_NAME).exists()) {
            cloneRepository()
            if (!File(POST_DIR).exists()) {
                File(POST_DIR).mkdir()
                File(ASSETS_DIR).mkdir()
                File(IMAGES_DIR).mkdir()
                val readme = "README.md"
                val format = SimpleDateFormat("yyyy-MM-dd")
                val targetReadMe = "${format.format(Date())}-$readme"
                File("$REPOSITORY_NAME/$readme").copyTo(File("$POST_DIR/$targetReadMe"))
                File("$REPOSITORY_NAME/$readme").copyTo(File("$IMAGES_DIR/$targetReadMe"))
                completeAll("init repo")
            }
        }
    }

    fun completeAll(message: String = "new article") {
        if (!isAllowed()) {
            cloneRepository()
        }
        addAll()
        commitAll(message)
        pullAll()
        pushAll()
    }

    fun pullAll() {
        if (isAllowed()) {
            git!!.pull().setTransportConfigCallback {
                (it as SshTransport).sshSessionFactory = getSshSessionFactory()
            }.call()
        }
    }

    fun setSsh(bytes: ByteArray) {
        val rsa = File(".ssh/id_rsa")
        if (rsa.exists()) {
            rsa.delete()
            rsa.writeBytes(bytes)
        } else {
            val sshDir = File(".ssh")
            if (!sshDir.exists()) sshDir.mkdir()
            rsa.writeBytes(bytes)
        }

        init()
        articleService.completeAll()
    }

    private fun isAllowed() = File(".ssh/id_rsa").exists() && File(REPOSITORY_NAME).exists()
}
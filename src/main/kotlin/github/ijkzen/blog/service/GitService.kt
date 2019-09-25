package github.ijkzen.blog.service

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import github.ijkzen.blog.utils.ASSETS_DIR
import github.ijkzen.blog.utils.IMAGES_DIR
import github.ijkzen.blog.utils.POST_DIR
import github.ijkzen.blog.utils.REPOSITORY_NAME
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.util.FS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

/**
 * @Author ijkzen
 * @Date 2019/9/25
 */
@Service
class GitService {

    companion object {
        private var git: Git? = null
            get() {
                if (field == null) {
                    field = Git.open(File(REPOSITORY_NAME))
                }
                return field
            }
    }

    @Autowired
    private lateinit var repositoryService: RepositoryService

    @Autowired
    private lateinit var developerService: DeveloperService

    fun cloneRepository() {
        val fullName = repositoryService.findAllRepos()[0].fullName
        Git.cloneRepository()
                .setURI("git@github.com:${fullName}.git")
                .setDirectory(File(REPOSITORY_NAME))
                .setTransportConfigCallback {
                    (it as SshTransport).sshSessionFactory = getSshSessionFactory()
                }
                .call()
    }

    fun addAll() {
        git!!.add().addFilepattern(".").call()
    }

    fun commitAll(message: String) {
        val developer = developerService.searchMaster()
        git!!.commit()
                .setAuthor(developer.developerName, developer.email)
                .setMessage(message).call()
    }

    fun pushAll() {
        git!!.push().setTransportConfigCallback {
            (it as SshTransport).sshSessionFactory = getSshSessionFactory()
        }.call()
    }

    fun init() {
        File(POST_DIR).mkdir()
        File(ASSETS_DIR).mkdir()
        File(IMAGES_DIR).mkdir()
        val readme = "README.md"
        File("$REPOSITORY_NAME/$readme").copyTo(File("$POST_DIR/$readme"))
        File("$REPOSITORY_NAME/$readme").copyTo(File("$IMAGES_DIR/$readme"))
        completeAll("init repo")
    }

    fun completeAll(message: String = "new article") {
        addAll()
        commitAll(message)
        pushAll()
    }

    fun pullAll() {
        git!!.pull().setTransportConfigCallback {
            (it as SshTransport).sshSessionFactory = getSshSessionFactory()
        }.call()
    }

    private fun getSshSessionFactory(): JschConfigSessionFactory {

        return object : JschConfigSessionFactory() {

            override fun configure(hc: OpenSshConfig.Host?, session: Session?) {
            }

            override fun createDefaultJSch(fs: FS?): JSch {
                val jsch = super.createDefaultJSch(fs)
                jsch.addIdentity(".ssh/id_rsa")
                return jsch
            }
        }
    }
}
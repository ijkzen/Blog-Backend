package github.ijkzen.blog.service

import github.ijkzen.blog.bean.github.response.DeveloperBean
import github.ijkzen.blog.repository.DeveloperRepository
import github.ijkzen.blog.utils.MASTER_ID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class DeveloperService {

    @Autowired
    private lateinit var repository: DeveloperRepository

    fun save(developerBean: DeveloperBean) {
        repository.save(developerBean)
    }

    fun searchMaster(): DeveloperBean {
        return repository.findById(File(MASTER_ID).readText().toLong()).get()
    }
}
package github.ijkzen.blog.service

import github.ijkzen.blog.bean.github.response.DeveloperBean
import github.ijkzen.blog.repository.DeveloperRepository
import github.ijkzen.blog.utils.MASTER
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeveloperService {

    @Autowired
    private lateinit var repository: DeveloperRepository

    fun save(developerBean: DeveloperBean) {
        repository.save(developerBean)
    }

    fun searchMaster(): Optional<DeveloperBean> {
        return repository.findDeveloperBeanByState(MASTER)
    }

    fun masterExists(): Boolean {
        return searchMaster().isPresent
    }

    fun searchDeveloperByNodeId(nodeId: String): DeveloperBean? {
        return repository.findDeveloperBeanByNodeId(nodeId)
    }

    fun searchDeveloperById(id: Long): DeveloperBean {
        return repository.findById(id).get()
    }

    fun searchDeveloperByName(name: String): DeveloperBean {
        return repository.findDeveloperBeanByDeveloperName(name)!!
    }
}
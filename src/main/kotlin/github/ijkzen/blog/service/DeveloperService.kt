package github.ijkzen.blog.service

import github.ijkzen.blog.bean.github.Developer
import github.ijkzen.blog.repository.DeveloperRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DeveloperService {

    @Autowired
    private lateinit var repository: DeveloperRepository

    fun save(developer: Developer) {
        repository.save(developer)
    }
}
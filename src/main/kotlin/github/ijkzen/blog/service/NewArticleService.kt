package github.ijkzen.blog.service

import github.ijkzen.blog.bean.articles.NewArticle
import github.ijkzen.blog.repository.NewArticleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @Author ijkzen
 * @Date 2019/9/29
 */

@Service
class NewArticleService {

    @Autowired
    private lateinit var newArticleRepository: NewArticleRepository

    fun save(newArticle: NewArticle) {
        newArticleRepository.save(newArticle)
    }

    fun find(id: Long): NewArticle {
        return newArticleRepository.findById(id).get()
    }

    fun getEditList(): List<NewArticle> {
        return newArticleRepository.findByProcessedFalse()
    }
}


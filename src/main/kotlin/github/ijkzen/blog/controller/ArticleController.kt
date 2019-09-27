package github.ijkzen.blog.controller

import org.springframework.web.bind.annotation.*

/**
 * @Author ijkzen
 * @Date 2019/9/27
 */
@RequestMapping
@RestController
class ArticleController {

    @PostMapping(value = ["/article"])
    fun addGuestArticle() {

    }

    @DeleteMapping
    fun deleteArticle() {
        //todo set `isShow` false
    }

    fun editArticle() {
        //todo new table to store info
    }

    fun updateArticle() {
        //todo update guest article content
    }

    @GetMapping(value = ["/articles"])
    fun getArticleList() {
        //todo by DESC or ASCE
    }

    fun searchArticles() {
        //todo by keywords at title and content
    }

    fun searchArticle() {
        //todo by id
    }

    fun getGuestArticleNotShow() {
        //todo
    }

}

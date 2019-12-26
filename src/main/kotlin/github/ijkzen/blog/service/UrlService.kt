package github.ijkzen.blog.service

import github.ijkzen.blog.bean.category.Category
import org.hibernate.Session
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Service
class UrlService {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Suppress("UNCHECKED_CAST")
    fun getUrlCount(): List<Category> {
        val list = LinkedList<Category>()
        val sql =
            "select RequestRecord.url, count(*) as size from RequestRecord group by url order by size desc limit 100"
        val session = entityManager.unwrap(Session::class.java)
        val results: List<Array<Any>> = session.createNativeQuery(sql).resultList as List<Array<Any>>
        results.forEach {
            val category = Category()
            category.category = it[0] as String
            category.size = (it[1] as BigInteger).toLong()
            list.add(category)
        }
        return list
    }
}
package github.ijkzen.blog.service

import com.alibaba.druid.pool.DruidDataSource
import github.ijkzen.blog.bean.category.Category
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class UrlService {

    @Autowired
    private lateinit var druidDataSource: DruidDataSource

    fun getUrlCount(): List<Category> {
        val list = LinkedList<Category>()
        val sql = "select RequestRecord.url, count(*) as size from RequestRecord group by url order by size desc"
        val stmt = druidDataSource.connection.createStatement()
        val resultSet = stmt.executeQuery(sql)
        while (resultSet.next()) {
            val item = Category()
            item.category = resultSet.getString("url")
            item.size = resultSet.getInt("size").toLong()
            list.add(item)
        }
        stmt.connection.close()
        stmt.close()
        return list
    }
}
package github.ijkzen.blog.repository

import github.ijkzen.blog.bean.record.IndexRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @Author ijkzen
 * @Date 2019/10/30
 */
@Repository
interface IndexRecordRepository : JpaRepository<IndexRecord, Long> {
}
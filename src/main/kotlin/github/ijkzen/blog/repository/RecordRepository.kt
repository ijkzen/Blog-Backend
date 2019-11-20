package github.ijkzen.blog.repository

import github.ijkzen.blog.bean.record.RequestRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecordRepository : JpaRepository<RequestRecord, Long> {

    fun findRequestRecordsByCountry(province: String): List<RequestRecord>
}
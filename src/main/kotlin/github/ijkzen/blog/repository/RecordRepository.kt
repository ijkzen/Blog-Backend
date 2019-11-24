package github.ijkzen.blog.repository

import github.ijkzen.blog.bean.record.RequestRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RecordRepository : JpaRepository<RequestRecord, Long> {

    fun findRequestRecordsByCountry(country: String): List<RequestRecord>

    fun findFirstByIpOrderByTime(ip: String): Optional<RequestRecord>
}
package github.ijkzen.blog.repository

import github.ijkzen.blog.bean.record.RequestRecord
import github.ijkzen.blog.utils.EMPTY
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RecordRepository : JpaRepository<RequestRecord, Long> {

    fun findRequestRecordsByCity(country: String): List<RequestRecord>

    fun findFirstByIpAndCityNotContaining(ip: String, city: String = EMPTY): Optional<RequestRecord>
}
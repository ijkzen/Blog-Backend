package github.ijkzen.blog.service

import github.ijkzen.blog.bean.record.IndexRecord
import github.ijkzen.blog.repository.IndexRecordRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @Author ijkzen
 * @Date 2019/10/30
 */
@Service
class IndexRecordService {

    @Autowired
    private lateinit var indexRecordRepository: IndexRecordRepository

    fun view() {
        val records = indexRecordRepository.findAll()
        if (records.isEmpty()) {
            val record = IndexRecord()
            indexRecordRepository.save(record)
        } else {
            indexRecordRepository.save(records[0].apply { count += 1 })
        }
    }

    fun getViewCount(): Long {
        val record = indexRecordRepository.findById(1)
        return record.get().count
    }
}
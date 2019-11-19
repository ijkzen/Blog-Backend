package github.ijkzen.blog.service

import github.ijkzen.blog.bean.record.CountBean
import github.ijkzen.blog.bean.record.RequestRecord
import github.ijkzen.blog.repository.RecordRepository
import github.ijkzen.blog.utils.EMPTY
import org.apache.commons.collections4.queue.CircularFifoQueue
import org.hibernate.Session
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ua_parser.Parser
import java.math.BigInteger
import java.util.*
import java.util.regex.Pattern
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional

@Service
class RecordService {

    companion object {
        const val USER_AGENT = "User-Agent"
    }

    @Autowired
    private lateinit var recordRepository: RecordRepository

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    private val logger = LoggerFactory.getLogger(javaClass)

    private val solidQueue = CircularFifoQueue<RequestRecord>()

    @Synchronized
    @Transactional
    fun saveRecord(request: HttpServletRequest) {
        val parser = Parser()
        val client = parser.parse(request.getHeader(USER_AGENT))
        val operatingSystem = client.os.family
        val operatingSystemVersion = client.os.major + "_" +
                client.os.minor + "_" +
                client.os.patch + "_" +
                client.os.patchMinor
        val browser = client.userAgent.family
        val browserVersion = client.userAgent.major + "_" +
                client.userAgent.minor + "_" +
                client.userAgent.patch

        val device = client.device.family
        val time = Date()
        var ip = request.getHeader("X-Real-IP")
        val tmp = request.requestURL.toString()
            .replace("https://", "")
            .replace("http://", "")

        val url = tmp.substring(
            tmp.indexOf("/")
        )
        val method = request.method

        ip = "220.181.38.148"
        if (ip != null && isIP(ip)) {
            val record = RequestRecord(
                operatingSystem = operatingSystem,
                operatingSystemVersion = operatingSystemVersion,
                browser = browser,
                browserVersion = browserVersion,
                device = device,
                time = time,
                ip = ip,
                url = url,
                httpMethod = method
            )
            batchInsertRecord()
            solidQueue.add(record)
        }
    }

    fun getReadyList(): List<RequestRecord> {
        return recordRepository.findRequestRecordsByProvince(EMPTY)
    }

    //todo frequency is too high
    fun save(requestRecord: RequestRecord) {
        recordRepository.save(requestRecord)
    }

    private fun isIP(ip: String): Boolean {
        val regex = "^((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(ip)
        return matcher.find() && ip != "127.0.0.1"
    }

    @Transactional
    fun getPeopleCount(): CountBean {
        val sql = "select count(distinct RequestRecord.ip)  as peopleCount from RequestRecord"
        val session = entityManager.unwrap(Session::class.java)
        val result: List<BigInteger> = session.createNativeQuery(sql).resultList as List<BigInteger>
        return CountBean().apply {
            count = result[0].toLong()
        }
    }

    @Transactional
    fun batchInsertRecord() {
        if (solidQueue.isAtFullCapacity) {
            val session = entityManager.unwrap(Session::class.java)
            logger.info("queue size: ${solidQueue.size}")
            solidQueue.forEach {
                logger.info("url: ${it.url}")
                session.save("RequestRecord", it)
            }
            solidQueue.clear()
        }
    }

}
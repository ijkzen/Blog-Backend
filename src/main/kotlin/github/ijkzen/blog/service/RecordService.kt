package github.ijkzen.blog.service

import github.ijkzen.blog.bean.record.CountBean
import github.ijkzen.blog.bean.record.IPCountBean
import github.ijkzen.blog.bean.record.IPCountsBean
import github.ijkzen.blog.bean.record.RequestRecord
import github.ijkzen.blog.repository.RecordRepository
import github.ijkzen.blog.utils.EMPTY
import org.hibernate.Session
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
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

    @Autowired
    private lateinit var handlerMapping: RequestMappingHandlerMapping

    private val logger = LoggerFactory.getLogger(javaClass)

    private val urlList = ArrayList<String>()

    fun initUrls() {
        if (urlList.isEmpty()) {
            val map: Map<RequestMappingInfo, HandlerMethod> = handlerMapping.handlerMethods
            map.forEach {
                it.key.patternsCondition.patterns.forEach { url ->
                    urlList.add(url)
                }
            }
        }
    }

    @Transactional
    fun saveRecord(request: HttpServletRequest) {
        val parser = Parser()
        initUrls()
        if (!request.getHeader(USER_AGENT).isNullOrEmpty()) {
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
            val ip = request.getHeader("X-Real-IP")
            val tmp = request.requestURL.toString()
                .replace("https://", "")
                .replace("http://", "")

            val url = tmp.substring(
                tmp.indexOf("/")
            )
            val method = request.method
            val record = RequestRecord(
                operatingSystem = operatingSystem,
                operatingSystemVersion = operatingSystemVersion,
                browser = browser,
                browserVersion = browserVersion,
                device = device,
                time = time,
                ip = ip ?: "",
                url = url,
                httpMethod = method
            )

            if (ip != null && isIP(record.ip) && checkUrl(url)) {
                save(record)
            }
        }
    }

    fun getReadyList(): List<RequestRecord> {
        return recordRepository.findRequestRecordsByCity(EMPTY)
    }

    @Transactional
    fun save(requestRecord: RequestRecord) {
        val session = entityManager.unwrap(Session::class.java)
        session.save("RequestRecord", requestRecord)
    }

    private fun isIP(ip: String): Boolean {
        val regex = "^((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(ip)
        return matcher.find() && ip != "127.0.0.1"
    }

    @Suppress("UNCHECKED_CAST")
    @Transactional
    fun getPeopleCount(): CountBean {
        val sql = "select count(distinct RequestRecord.ip)  as peopleCount from RequestRecord"
        val session = entityManager.unwrap(Session::class.java)
        val result: List<BigInteger> = session.createNativeQuery(sql).resultList as List<BigInteger>
        return CountBean().apply {
            count = result[0].toLong()
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Transactional
    fun getIpCount(): IPCountsBean {
        val sql =
            "select country, region, city, count(*) as size from RequestRecord where country!='empty' group by country, region, city order by size desc limit 100";
        val session = entityManager.unwrap(Session::class.java)
        val result: List<Array<Any>> = session.createNativeQuery(sql).resultList as List<Array<Any>>
        val list = LinkedList<IPCountBean>()
        result.forEach {
            val item = IPCountBean()
            item.country = it[0] as String
            item.region = it[1] as String
            item.city = it[2] as String
            item.size = (it[3] as BigInteger).toLong()
            list.add(item)
        }
        return IPCountsBean(list)
    }

    fun getRecordByIp(ip: String): Optional<RequestRecord> {
        return recordRepository.findFirstByIpAndCityNotContaining(ip)
    }

    fun checkUrl(url: String): Boolean {
        return urlList.contains(url)
    }
}
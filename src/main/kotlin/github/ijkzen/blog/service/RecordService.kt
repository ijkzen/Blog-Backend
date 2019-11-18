package github.ijkzen.blog.service

import com.alibaba.druid.pool.DruidDataSource
import github.ijkzen.blog.bean.record.CountBean
import github.ijkzen.blog.bean.record.RequestRecord
import github.ijkzen.blog.repository.RecordRepository
import github.ijkzen.blog.utils.EMPTY
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ua_parser.Parser
import java.util.*
import java.util.regex.Pattern
import javax.servlet.http.HttpServletRequest

@Service
class RecordService {

    companion object {
        const val USER_AGENT = "User-Agent"
    }

    @Autowired
    private lateinit var recordRepository: RecordRepository

    @Autowired
    private lateinit var druidDataSource: DruidDataSource

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
            ip = ip,
            url = url,
            httpMethod = method
        )

        if (isIP(record.ip)) {
            recordRepository.save(record)
        }
    }

    fun getReadyList(): List<RequestRecord> {
        return recordRepository.findRequestRecordsByProvince(EMPTY)
    }

    fun save(requestRecord: RequestRecord) {
        recordRepository.save(requestRecord)
    }

    private fun isIP(ip: String): Boolean {
        val regex = "^((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(ip)
        return matcher.find() && ip != "127.0.0.1"
    }

    fun getPeopleCount(): CountBean {
        val sql = "select count(distinct RequestRecord.ip)  as peopleCount from RequestRecord"
        val stmt = druidDataSource.connection.createStatement()
        val resultSet = stmt.executeQuery(sql)
        val result = CountBean()
        while (resultSet.next()) {
            result.count = resultSet.getLong("peopleCount")
        }
        stmt.connection.close()
        stmt.close()
        return result
    }
}
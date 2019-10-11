package github.ijkzen.blog.service

import github.ijkzen.blog.bean.record.RequestRecord
import github.ijkzen.blog.repository.RecordRepository
import github.ijkzen.blog.utils.EMPTY
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ua_parser.Parser
import java.util.*
import javax.servlet.http.HttpServletRequest

@Service
class RecordService {

    companion object {
        const val USER_AGENT = "User-Agent"
    }

    @Autowired
    private lateinit var recordRepository: RecordRepository

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
        val ip = "114.247.50.2"//request.remoteHost //todo
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

        recordRepository.save(record)
    }

    fun getReadyList(): List<RequestRecord> {
        return recordRepository.findRequestRecordsByProvince(EMPTY)
    }

    fun save(requestRecord: RequestRecord) {
        recordRepository.save(requestRecord)
    }
}
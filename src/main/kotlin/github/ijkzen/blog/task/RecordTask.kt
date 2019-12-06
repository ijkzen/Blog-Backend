package github.ijkzen.blog.task

import github.ijkzen.blog.bean.record.IPAddress
import github.ijkzen.blog.service.RecordService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.*
import javax.transaction.Transactional
import kotlin.collections.ArrayList


@Component
class RecordTask {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var recordService: RecordService

    private val restTemplate = RestTemplate()

    init {
        val messageConverters = ArrayList<HttpMessageConverter<*>>()
        val converter = MappingJackson2HttpMessageConverter()

        converter.supportedMediaTypes = Collections.singletonList(MediaType.ALL)
        messageConverters.add(converter)
        restTemplate.messageConverters = messageConverters
    }

    @Suppress("UNCHECKED_CAST")
    @Async
    @Scheduled(fixedRate = 1000 * 60 * 60 * 12)
    @Transactional
    fun completeRecord() {
        val list = recordService.getReadyList()
        list.forEach {
            logger.info("ip: ${it.ip}")
            val optional = recordService.getRecordByIp(it.ip)
            if (optional.isPresent) {
                val origin = optional.get()
                it.country = origin.country
                it.region = origin.region
                it.city = origin.city
            } else {
                val json = restTemplate.getForObject(
                    "http://ip-api.com/json/${it.ip}?lang=zh-CN",
                    IPAddress::class.java
                )

                Thread.sleep(5000)
                it.country = json!!.country
                it.region = json.region
                it.city = json.city
            }

            recordService.save(it)
        }
    }

}
package github.ijkzen.blog.task

import github.ijkzen.blog.service.RecordService
import github.ijkzen.blog.utils.AMAP_KEY
import org.codehaus.jackson.map.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class RecordTask {

    @Autowired
    private lateinit var recordService: RecordService

    private val restTemplate = RestTemplate()

    @Suppress("UNCHECKED_CAST")
    @Async
    @Scheduled(fixedRate = 1000 * 60 * 60 * 12)
    fun completeRecord() {
        val list = recordService.getReadyList()
        list.forEach {
            val json = restTemplate.getForObject(
                    "https://restapi.amap.com/v3/ip?ip=${it.ip}&key=$AMAP_KEY",
                    String::class.java
            )
            val map: Map<String, String> = ObjectMapper().readValue(json, Map::class.java) as Map<String, String>
            it.province = map["province"] ?: ""
            it.city = map["city"] ?: ""
            it.longitude = (map["rectangle"] ?: "")
                    .split(";")[0]
                    .split(",")[0]
            it.latitude = (map["rectangle"] ?: "")
                    .split(";")[0]
                    .split(",")[1]
//todo
            //recordService.save(it)
        }
    }

}
package github.ijkzen.blog

import github.ijkzen.blog.bean.xiaoice.ChatMessagesBean
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import java.io.UnsupportedEncodingException
import java.util.*
import java.util.regex.Pattern


/**
 * @Author ijkzen
 * @Date 2019/10/26
 */

class XiaoIceTest {

    private val restTemplate = RestTemplate()

    private val headers = HttpHeaders()

    companion object {
        var retryCount = 0;
    }

    @Before
    fun init() {
        headers.accept = Collections.singletonList(MediaType.APPLICATION_JSON_UTF8)
        headers["Host"] = "m.weibo.cn"
        headers["User-Agent"] = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0"
        headers["Cookie"] = "MLOGIN=1; _T_WM=66979154865; XSRF-TOKEN=9ddfc2; M_WEIBOCN_PARAMS=luicode%3D20000174%26lfid%3D102803; SUB=_2A25wsEDADeRhGeNJ6loV9irEzDqIHXVQW2CIrDV6PUJbkdANLWnFkW1NS_kFlWRtDv-tig3_5nPKJ9LwWTls6dSA; SUHB=09P9eSFRigcg0W; SCF=AhG1hUj3_7emcM1SPS6ULLgGXHg89EGYkdvciXLKPKSaV7MMLT-ocIxsdW-Pqwv3Y8n2oJ9WNmq3swXabOd6juo.; SSOLoginState=1572090000"
    }

    fun getChatMessagesListSize(): Long {
        val entity = HttpEntity<String>("", headers)
        val response = restTemplate.exchange(
                "https://m.weibo.cn/msg/messages?uid=5175429989",
                HttpMethod.GET,
                entity,
                ChatMessagesBean::class.java
        )
        return response.body?.total!!
    }

    fun sendMessage() {
        val message = getRandomJianHan(10)
        val map = LinkedMultiValueMap<String, String?>()
        map.add("fileId", null)
        map.add("uid", "5175429989")
        map.add("content", message)
        map.add("st", "a5c058")
        val entity = HttpEntity(map, getFormHeaders())
        val response = restTemplate.exchange(
                "https://m.weibo.cn/msgDeal/sendMsg?",
                HttpMethod.POST,
                entity,
                String::class.java
        )
    }

    fun getNewMessages() {
//        val originSize = getChatMessagesListSize()
//        println("origin size: $originSize")
//        if (retryCount == 0) {
//            sendMessage()
//        }
//        val currentSize = getChatMessagesListSize()
//        if (currentSize <= originSize + 1 && retryCount < 10) {
//            println("retry: $retryCount\ncurrent size: $currentSize\norigin size: $originSize")
//            retryCount++
//            getNewMessages()
//        } else {
//            retryCount = 0
//            println("final size: $currentSize\norigin size: $originSize")
//        }


        sendMessage()
        val originSize = getChatMessagesListSize()
        Thread.sleep(3000)
        val currentSize = getChatMessagesListSize()
        if (currentSize > originSize) {
            println("currentSize: $currentSize")
        } else {
            println("get list after 2000ms failed")
        }
    }

    private fun getJsonHeaders(): HttpHeaders {
        return headers.apply {
            this.accept = Collections.singletonList(MediaType.APPLICATION_JSON_UTF8)
        }
    }

    private fun getFormHeaders(): HttpHeaders {
        return headers.apply {
            this["Content-Type"] = MediaType.APPLICATION_FORM_URLENCODED_VALUE
            this["TE"] = "Trailers"
            this["Referer"] = "https://m.weibo.cn/msg/chat?uid=5175429989&nick=%E5%B0%8F%E5%86%B0&verified_type=0&send_from=user_profile&luicode=10000011&lfid=1005055175429989"
            this["Origin"] = "https://m.weibo.cn"
        }
    }

    private fun getChatMessagesList(): ChatMessagesBean {
        val entity = HttpEntity<String>("", headers)
        val response = restTemplate.exchange(
                "https://m.weibo.cn/msg/messages?uid=5175429989",
                HttpMethod.GET,
                entity,
                ChatMessagesBean::class.java
        )
        response.body?.data?.forEach {
            println(it)
        }
        return response.body!!
    }

    fun getRandomJianHan(len: Int): String {
        var ret = ""
        for (i in 0 until len) {
            var str: String? = null
            val hightPos: Int
            val lowPos: Int // 定义高低位
            val random = Random()
            hightPos = 176 + Math.abs(random.nextInt(39)) // 获取高位值
            lowPos = 161 + Math.abs(random.nextInt(93)) // 获取低位值
            val b = ByteArray(2)
            b[0] = hightPos.toByte()
            b[1] = lowPos.toByte()
            try {
                str = String(b, charset("GBK")) // 转成中文
            } catch (ex: UnsupportedEncodingException) {
                ex.printStackTrace()
            }

            ret += str
        }
        return ret
    }

    @Test
    fun getST() {
        val entity = HttpEntity("", getSTHeaders())
        val response = restTemplate.exchange(
                "https://m.weibo.cn/msg/chat?uid=5175429989&nick=%E5%B0%8F%E5%86%B0&verified_type=0&send_from=user_profile&luicode=10000011&lfid=1005055175429989",
                HttpMethod.GET,
                entity,
                String::class.java
        )

        val regex = "\"st\":\"([a-zA-Z0-9]{6})\""
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(response.body!!)
        if (matcher.find()) {
            println(matcher.group(1))
        }
    }

    private fun getSTHeaders(): HttpHeaders {
        return headers.apply {
            this.accept = Collections.singletonList(MediaType.TEXT_HTML)
            this.remove("Content-Type")
            this.remove("TE")
            this.remove("Referer")
            this.remove("Origin")
        }
    }
}
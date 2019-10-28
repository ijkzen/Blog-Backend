package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.xiaoice.ChatMessagesBean
import github.ijkzen.blog.bean.xiaoice.MessagesBean
import github.ijkzen.blog.service.DeveloperService
import github.ijkzen.blog.utils.AUTHORIZATION
import github.ijkzen.blog.utils.getAuthentication
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.util.*

/**
 * @Author ijkzen
 * @Date 2019/10/27
 */
@Api(value = "和小冰聊天", tags = ["小冰"])
@RequestMapping(value = ["/chat"])
@RestController
class ChatController {

    private val restTemplate = RestTemplate()

    private val headers = HttpHeaders()

    @Autowired
    private lateinit var developerService: DeveloperService

    init {
        headers.accept = Collections.singletonList(MediaType.APPLICATION_JSON_UTF8)
        headers["Host"] = "m.weibo.cn"
        headers["User-Agent"] = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0"
        headers["Cookie"] = "_T_WM=50522080483; SUB=_2A25wtrMqDeRhGeNJ6loV9irEzDqIHXVQWN1irDV6PUJbkdAKLUzEkW1NS_kFlUD1QPkGQ-_6yyHvTswII62Jj-Zv; SUHB=0y0ZqRaZQ_SBI6; MLOGIN=1; XSRF-TOKEN=403239; M_WEIBOCN_PARAMS=luicode%3D10000011%26lfid%3D1076035175429989"
    }


    @ApiOperation(
            value = "给小冰发送消息，小冰回话",
            notes =
            """
                由于微博API的限制，消息的发送和列表分为了两个API，
                而且消息回复还有时差，所以这个API响应时间较长，大概在5-7s
            """
    )
    @ApiImplicitParams(
            ApiImplicitParam(
                    name = AUTHORIZATION,
                    value = "验证身份",
                    required = true,
                    dataTypeClass = String::class,
                    paramType = "header"
            ),
            ApiImplicitParam(
                    name = "message",
                    value = "发送的消息",
                    required = true
            )
    )
    @GetMapping("/{message}")
    fun getAnswer(@PathVariable("message") message: String): MessagesBean {
        val result = MessagesBean()
        val authorization = getAuthentication()
        val developer = developerService.searchDeveloperByNodeId(authorization!!.principal.toString())
        return if (developer?.nodeId != null) {
            sendMessage(message)
            val originSize = getChatMessagesList().data.size
            Thread.sleep(5000)
            val currentSize = getChatMessagesList().data.size
            result.apply {
                this.list = getChatMessagesList().data.subList(0, currentSize - originSize + 1)
            }
        } else {
            return result.apply {
                this.errCode = "401"
                this.errMessage = "权限不足"
            }
        }
    }

    private fun sendMessage(message: String) {
        val map = LinkedMultiValueMap<String, String?>()
        map.add("fileId", null)
        map.add("uid", "5175429989")
        map.add("content", message)
        map.add("st", "09e475")
        val entity = HttpEntity(map, getFormHeaders())
        val response = restTemplate.exchange(
                "https://m.weibo.cn/msgDeal/sendMsg?",
                HttpMethod.POST,
                entity,
                String::class.java
        )
        println(response.body!!)
    }

    private fun getChatMessagesList(): ChatMessagesBean {
        val entity = HttpEntity<String>("", getJsonHeaders())
        val response = restTemplate.exchange(
                "https://m.weibo.cn/msg/messages?uid=5175429989",
                HttpMethod.GET,
                entity,
                ChatMessagesBean::class.java
        )
        println("size: ${response.body?.total}")
        return response.body!!
    }

    private fun getFormHeaders(): HttpHeaders {
        return headers.apply {
            this["Content-Type"] = MediaType.APPLICATION_FORM_URLENCODED_VALUE
            this["TE"] = "Trailers"
            this["Referer"] = "https://m.weibo.cn/msg/chat?uid=5175429989&nick=%E5%B0%8F%E5%86%B0&verified_type=0&send_from=user_profile&luicode=10000011&lfid=1005055175429989"
            this["Origin"] = "https://m.weibo.cn"
        }
    }

    private fun getJsonHeaders(): HttpHeaders {
        return headers.apply {
            this.accept = Collections.singletonList(MediaType.APPLICATION_JSON_UTF8)
            this.remove("Content-Type")
            this.remove("TE")
            this.remove("Referer")
            this.remove("Origin")
        }
    }
}
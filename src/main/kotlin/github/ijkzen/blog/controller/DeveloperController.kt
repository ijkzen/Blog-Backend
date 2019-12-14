package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.BaseBean
import github.ijkzen.blog.bean.developer.Developer
import github.ijkzen.blog.bean.github.response.DeveloperBean
import github.ijkzen.blog.service.DeveloperService
import github.ijkzen.blog.utils.AUTHORIZATION
import github.ijkzen.blog.utils.getAuthentication
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.net.URL

/**
 * @Author ijkzen
 * @Date 2019/10/29
 */
@Api(value = "开发者", description = "开发者相关", tags = ["开发者"])
@RestController
@RequestMapping("/developer")
class DeveloperController {

    @Autowired
    private lateinit var developerService: DeveloperService

    @GetMapping("/info")
    @ApiOperation(
        value = "获取开发者信息",
        notes =
        """
                获取开发者信息，需要验证身份
            """
    )
    @ApiImplicitParams(
        ApiImplicitParam(
            name = AUTHORIZATION,
            value = "验证身份",
            required = true,
            dataTypeClass = String::class,
            paramType = "header"
        )
    )
    fun getDeveloperInfo(): Developer {
        val authentication = getAuthentication()
        val developer = developerService.searchDeveloperByNodeId(authentication!!.principal.toString())
        val result = DeveloperBean()
        return Developer(
            result.apply {
                developer!!
                developerId = developer.developerId
                developerName = developer.developerName
                nodeId = developer.nodeId
                avatarUrl = developer.avatarUrl
                htmlUrl = developer.htmlUrl
            })
    }


    @ApiOperation(
        value = "验证是否为站长",
        notes =
        """
            如果是站长则errCode为000，如果不是则errCode为401 
        """
    )
    @ApiImplicitParams(
        ApiImplicitParam(
            name = AUTHORIZATION,
            value = "验证身份",
            required = true,
            dataTypeClass = String::class,
            paramType = "header"
        )
    )
    @GetMapping("/master")
    fun checkMaster(): BaseBean {
        val master = developerService.searchMaster().get()
        val authentication = getAuthentication()
        val result = BaseBean()
        return if (master.nodeId == authentication!!.principal) {
            result
        } else {
            result.apply {
                errCode = "500"
                errMessage = "当前用户不是站长"
            }
        }
    }

    @ApiOperation(
        value = "get developer avatar by id",
        notes = "return error jpg if id not exists"
    )
    @ResponseBody
    @GetMapping("/avatar/{id}", produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getAvatar(@PathVariable("id") id: Long): ByteArray {
        val developer = developerService.searchDeveloperById(id)
        return URL(developer.avatarUrl).readBytes()
    }
}
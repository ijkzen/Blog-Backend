package github.ijkzen.blog.controller

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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @Author ijkzen
 * @Date 2019/10/29
 */
@Api(value = "开发者", tags = ["开发者"])
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
}
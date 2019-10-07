package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.BaseBean
import github.ijkzen.blog.bean.mail.Mail
import github.ijkzen.blog.service.DeveloperService
import github.ijkzen.blog.service.MailService
import github.ijkzen.blog.utils.AUTHORIZATION
import github.ijkzen.blog.utils.getAuthentication
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @Author ijkzen
 * @Date 2019/10/6
 */
@Api(
        value = "邮件相关",
        tags = ["邮件相关"],
        description = "检查权限"
)
@RequestMapping("/mail")
@RestController
class MailController {

    @Autowired
    private lateinit var mailService: MailService

    @Autowired
    private lateinit var developerService: DeveloperService

    @ApiOperation(
            value = "添加邮箱",
            notes =
            """
                添加新的邮箱后，之前所设置的所有邮箱都会失效   
                需要检查权限，仅站长可以设置
            """
    )
    @ApiImplicitParams(
            ApiImplicitParam(
                    name = "mail",
                    value = "邮件配置信息",
                    required = true,
                    dataType = "Mail",
                    dataTypeClass = Mail::class,
                    paramType = "body"
            ),
            ApiImplicitParam(
                    name = AUTHORIZATION,
                    value = "验证身份",
                    required = true,
                    dataTypeClass = String::class,
                    paramType = "header"
            )
    )
    @PostMapping(value = ["/new"])
    fun save(@RequestBody mail: Mail): BaseBean {
        val result = BaseBean()
        val authentication = getAuthentication()
        val master = developerService.searchMaster()
        return if (authentication!!.principal == master.nodeId) {
            mailService.save(
                    mail.apply {
                        id = null
                    }
            )
            result.apply {
                errMessage = "添加邮箱成功"
            }
        } else {
            result.apply {
                errCode = "401"
                errMessage = "权限不足"
            }
        }
    }
}
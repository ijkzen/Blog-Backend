package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.BaseBean
import github.ijkzen.blog.service.DeveloperService
import github.ijkzen.blog.service.GitService
import github.ijkzen.blog.utils.AUTHORIZATION
import github.ijkzen.blog.utils.getAuthentication
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Api(
    value = "Github私钥",
    tags = ["rsa"],
    description = "用于提交代码，绝不会用作他途"
)
@RestController
@RequestMapping("/ssh")
class SshController {

    @Autowired
    private lateinit var developerService: DeveloperService

    @Autowired
    private lateinit var gitService: GitService

    @ApiOperation(
        value = "接收Github私钥文件",
        notes =
        """
                用于提交代码，绝不会用作他途，需要验证权限  
            """
    )
    @ApiImplicitParam(
        name = AUTHORIZATION,
        value = "验证身份",
        required = true,
        dataTypeClass = String::class,
        paramType = "header"
    )
    @PostMapping("/new")
    @ResponseBody
    fun newSsh(ssh: MultipartFile): BaseBean {
        val authentication = getAuthentication()
        val master = developerService.searchMaster().get()
        return if (authentication!!.principal == master.nodeId) {
            Thread {
                gitService.setSsh(ssh.bytes)
            }.start()
            BaseBean()
        } else {
            BaseBean("401", "权限不足")
        }
    }
}
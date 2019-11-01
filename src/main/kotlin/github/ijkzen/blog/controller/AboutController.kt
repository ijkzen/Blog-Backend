package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.BaseBean
import github.ijkzen.blog.service.DeveloperService
import github.ijkzen.blog.service.GitService
import github.ijkzen.blog.utils.ABOUT_MD
import github.ijkzen.blog.utils.AUTHORIZATION
import github.ijkzen.blog.utils.getAuthentication
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.io.File

@Api(
    value = "关于我的一些信息",
    description = "关于我",
    tags = ["关于"]
)
@RestController
@RequestMapping("/about")
class AboutController {

    @Autowired
    private lateinit var developerService: DeveloperService

    @Autowired
    private lateinit var gitService: GitService

    @ApiOperation(
        value = "获取about信息",
        notes =
        """
                不需要权限
            """
    )
    @GetMapping("/me")
    fun getAbout(): AboutBean {
        return AboutBean().apply {
            if (File(ABOUT_MD).exists()) {
                about = File(ABOUT_MD).readText()
            } else {
                errCode = "404"
                about = "还没有添加关于信息，先看看别的吧"
            }
        }
    }

    @ApiOperation(
        value = "设置about信息",
        notes =
        """
                需要验证权限，只有站长可以设置；
                about字段是markdown格式
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
            name = "about",
            value = "关于信息",
            required = true,
            dataTypeClass = AboutBean::class,
            dataType = "AboutBean"
        )
    )
    @PostMapping("/me")
    fun setAbout(@RequestBody about: AboutBean): BaseBean {
        val authentication = getAuthentication()
        val master = developerService.searchMaster()
        return if (authentication!!.principal == master.nodeId) {
            File(ABOUT_MD).writeText(about.about!!)
            gitService.completeAll("update about.md")
            BaseBean()
        } else {
            BaseBean().apply {
                errCode = "401"
                errMessage = "权限不足"
            }
        }
    }
}

data class AboutBean(var about: String? = null) : BaseBean()

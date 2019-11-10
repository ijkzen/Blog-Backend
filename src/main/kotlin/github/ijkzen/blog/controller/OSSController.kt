package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.BaseBean
import github.ijkzen.blog.bean.oss.OSS
import github.ijkzen.blog.repository.OSSRepository
import github.ijkzen.blog.service.ArticleService
import github.ijkzen.blog.service.DeveloperService
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

@Api(
    value = "对象存储",
    tags = ["对象存储"],
    description = "支持七牛和阿里云的OSS"
)
@RestController
@RequestMapping("/oss")
class OSSController {

    @Autowired
    private lateinit var ossRepository: OSSRepository

    @Autowired
    private lateinit var articleService: ArticleService

    @Autowired
    private lateinit var developerService: DeveloperService

    @ApiOperation(
        value = "设置OSS",
        notes =
        """
            设置OSS，同步修改文章内的图片URL，需要验证权限    
        """
    )
    @ApiImplicitParams(
        ApiImplicitParam(
            name = "newOSS",
            value = "OSS主体",
            required = true,
            dataType = "OSS",
            dataTypeClass = OSS::class,
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
    @PostMapping("/set")
    fun setOSS(@RequestBody newOSS: OSS): BaseBean {
        val authentication = getAuthentication()
        val master = developerService.searchMaster()
        return if (authentication!!.principal == master.nodeId) {
            ossRepository.deleteUseless()
            newOSS.id = null
            newOSS.inUse = true
            ossRepository.save(newOSS)

            Thread {
                articleService.completeAll()
            }.start()

            BaseBean()
        } else {
            BaseBean().apply {
                errMessage = "权限不足"
                errCode = "401"
            }
        }
    }
}
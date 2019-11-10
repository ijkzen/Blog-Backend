package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.category.CategoryBean
import github.ijkzen.blog.service.UrlService
import github.ijkzen.blog.utils.AUTHORIZATION
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(
    value = "服务请求统计",
    tags = ["统计"]
)
@RestController
@RequestMapping("/url")
class UrlController {

    @Autowired
    private lateinit var urlService: UrlService

    @ApiOperation(
        value = "请求次数统计",
        notes =
        """
            需要权限    
        """
    )
    @ApiImplicitParam(
        name = AUTHORIZATION,
        value = "验证身份",
        required = true,
        dataTypeClass = String::class,
        paramType = "header"
    )
    @GetMapping("/list")
    fun getUrlCount(): CategoryBean {
        return CategoryBean()
            .apply {
                list = urlService.getUrlCount()
            }
    }
}
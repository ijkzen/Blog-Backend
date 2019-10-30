package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.BaseBean
import github.ijkzen.blog.bean.record.CountBean
import github.ijkzen.blog.service.IndexRecordService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @Author ijkzen
 * @Date 2019/10/30
 */
@Api(value = "记录首页的相关数据", description = "首页访问数据", tags = ["首页"])
@RequestMapping("/index")
@RestController
class IndexController {

    @Autowired
    private lateinit var indexService: IndexRecordService

    @ApiOperation(
        value = "记录首页访问数据",
        notes =
        """
            不需要权限    
        """
    )
    @GetMapping("/count")
    fun count(): BaseBean {
        indexService.view()
        return BaseBean()
    }

    @ApiOperation(
        value = "获取当前首页访问量",
        notes =
        """
            不需要权限    
        """
    )
    @GetMapping("view")
    fun view(): CountBean {
        val result = CountBean()
        return result.apply {
            count = indexService.getViewCount()
        }
    }
}
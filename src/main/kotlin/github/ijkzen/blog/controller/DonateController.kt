package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.BaseBean
import github.ijkzen.blog.service.DonateService
import io.swagger.annotations.Api
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Api(value = "")
@RequestMapping(value = ["/donate"])
@Controller
class DonateController {

    @Autowired
    private lateinit var donateService: DonateService

    @PostMapping(value = ["/alipay"])
    fun receiveAliPay(@RequestParam("alipay") file: MultipartFile): BaseBean {
        val result = BaseBean()
        if (!file.isEmpty) {
            donateService.receiveAliPay(file.bytes)
        } else {
            result.errCode = "500"
            result.errMessage = "上传失败"
        }
        return result
    }
}
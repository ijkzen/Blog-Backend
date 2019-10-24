package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.BaseBean
import github.ijkzen.blog.service.DeveloperService
import github.ijkzen.blog.service.DonateService
import github.ijkzen.blog.utils.AUTHORIZATION
import github.ijkzen.blog.utils.getAuthentication
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.net.URI

@Api(value = "捐赠相关", description = "存取捐赠的二维码", tags = ["二维码"])
@RequestMapping(value = ["/donate"])
@Controller
class DonateController {

    @Autowired
    private lateinit var donateService: DonateService

    @Autowired
    private lateinit var developerService: DeveloperService

    @ApiOperation(
            value = "接收支付宝收款二维码图片",
            notes =
            """
                如果文件为空，会返回错误信息    
            """
    )
    @ApiImplicitParam(
            name = AUTHORIZATION,
            value = "验证身份",
            required = true,
            dataTypeClass = String::class,
            paramType = "header"
    )
    @PostMapping(value = ["/alipay"])
    @ResponseBody
    fun receiveAliPay(alipay: MultipartFile): BaseBean {
        val result = BaseBean()
        val authorization = getAuthentication()
        val master = developerService.searchMaster()
        if (authorization!!.principal == master.nodeId) {
            if (!alipay.isEmpty) {
                donateService.receiveAliPay(alipay.bytes)
            } else {
                result.errCode = "500"
                result.errMessage = "上传失败"
            }
        } else {
            result.errCode = "401"
            result.errMessage = "权限不足"
        }
        return result
    }

    @ApiOperation(
            value = "接收微信支付收款二维码图片",
            notes =
            """
                如果文件为空，会返回错误信息    
            """
    )
    @ApiImplicitParam(
            name = AUTHORIZATION,
            value = "验证身份",
            required = true,
            dataTypeClass = String::class,
            paramType = "header"
    )
    @PostMapping(value = ["/wechat"])
    @ResponseBody
    fun receiveWechat(wechat: MultipartFile): BaseBean {
        val result = BaseBean()
        val authorization = getAuthentication()
        val master = developerService.searchMaster()
        if (authorization!!.principal == master.nodeId) {
            if (!wechat.isEmpty) {
                donateService.receiveWechatPay(wechat.bytes)
            } else {
                result.errCode = "500"
                result.errMessage = "上传失败"
            }
        } else {
            result.errCode = "401"
            result.errMessage = "权限不足"
        }
        return result
    }

    @ApiOperation(
            value = "获取支付宝付款图片",
            notes =
            """
                当图片不存在时，返回图片错误
            """
    )
    @ResponseBody
    @GetMapping(value = ["/alipay"], produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getAlipay(): ByteArray {
        return if (donateService.transformAliPay().exists()) {
            donateService.transformAliPay().readBytes()
        } else {
            URI("https://cdn.nextto.top/no_pic.png").toURL().readBytes()
        }
    }

    @ApiOperation(
            value = "获取微信付款图片",
            notes =
            """
                当图片不存在时，返回图片错误
            """
    )
    @ResponseBody
    @GetMapping(value = ["/wechat"], produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getWechat(): ByteArray {
        return if (donateService.transformAliPay().exists()) {
            donateService.transformWechatPay().readBytes()
        } else {
            URI("https://cdn.nextto.top/no_pic.png").toURL().readBytes()
        }
    }
}
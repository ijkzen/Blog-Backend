package github.ijkzen.blog.service

import github.ijkzen.blog.utils.IMAGES_DIR
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class DonateService {

    companion object {
        const val ALIPAY = "$IMAGES_DIR/alipay.png"
        const val WECHAT_PAY = "$IMAGES_DIR/wechat.png"
    }

    @Autowired
    private lateinit var gitService: GitService

    fun receiveAliPay(file: ByteArray) {
        val result = File(ALIPAY)
        result.writeBytes(file)
        gitService.completeAll("update alipay qrcode")
    }

    fun receiveWechatPay(file: ByteArray) {
        val result = File(WECHAT_PAY)
        result.writeBytes(file)
        gitService.completeAll("update wechat qrcode")
    }

    fun transformAliPay(): File {

        return File(ALIPAY)
    }

    fun transformWechatPay(): File {

        return File(WECHAT_PAY)
    }
}
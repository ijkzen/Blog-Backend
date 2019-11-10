package github.ijkzen.blog.service.oos

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.qiniu.storage.Configuration
import com.qiniu.storage.Region
import com.qiniu.storage.UploadManager
import com.qiniu.storage.model.DefaultPutRet
import com.qiniu.util.Auth
import github.ijkzen.blog.bean.oss.OSS
import github.ijkzen.blog.repository.OSSRepository
import github.ijkzen.blog.utils.ASSETS_DIR
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

/**
 * @Author ijkzen
 * @Date 2019/9/26
 */
@Service
class QiNiuOSS : AbstractOSS() {

    @Autowired
    private lateinit var ossRepository: OSSRepository

    fun getUploadToken(key: String): String {
        val oos = getOssInUse() ?: throw NullPointerException("当前无可用的对象存储信息")
        return Auth.create(oos.accessKey, oos.secretKey).uploadToken(oos.bucket, key)
    }

    var uploadManager: UploadManager? = null
        get() {
            if (field == null) {
                field = UploadManager(Configuration(Region.region0()))
            }
            return field
        }

    fun getOssInUse(): OSS? {
        val list = ossRepository.findByInUseIsTrue()
        return if (list == null || list.isEmpty()) {
            null
        } else {
            list[0]
        }
    }

    // fileName: images/2019/09/27/xxx.png
    override fun uploadFile(fileName: String) {
        val key = fileName.replace("\\", "/")
        val file = File("$ASSETS_DIR/$key")
        val response = uploadManager!!.put(
            file, key,
            getUploadToken(key)
        )
        val result = ObjectMapper().readValue<DefaultPutRet>(response.bodyString())
        if (key != result.key) throw Exception("上传失败")
    }
}
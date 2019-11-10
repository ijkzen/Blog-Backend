package github.ijkzen.blog.service.oos

import com.aliyun.oss.OSSClientBuilder
import com.aliyun.oss.model.PutObjectRequest
import github.ijkzen.blog.bean.oss.OSS
import github.ijkzen.blog.repository.OSSRepository
import github.ijkzen.blog.utils.ASSETS_DIR
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class AliyunOSS : AbstractOSS() {

    @Autowired
    private lateinit var ossRepository: OSSRepository

    fun getOssInUse(): OSS? {
        val list = ossRepository.findByInUseIsTrue()
        return if (list == null || list.isEmpty()) {
            null
        } else {
            list[0]
        }
    }

    override fun uploadFile(fileName: String) {
        val key = fileName.replace("\\", "/")
        val file = File("$ASSETS_DIR/$key")
        val oss = getOssInUse()
        if (oss == null) {
            throw NullPointerException("当前未配置对象存储")
        } else {
            val endpoint = "http://oss-cn-hangzhou.aliyuncs.com"
            val ossClient = OSSClientBuilder().build(endpoint, oss.accessKey, oss.secretKey)
            val putObjectRequest = PutObjectRequest(oss.bucket, key, file)
            ossClient.putObject(putObjectRequest)
            ossClient.shutdown()
        }
    }
}
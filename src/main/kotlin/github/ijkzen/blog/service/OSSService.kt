package github.ijkzen.blog.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.qiniu.storage.Configuration
import com.qiniu.storage.Region
import com.qiniu.storage.UploadManager
import com.qiniu.storage.model.DefaultPutRet
import com.qiniu.util.Auth
import github.ijkzen.blog.bean.oss.OSS
import github.ijkzen.blog.repository.OSSRepository
import github.ijkzen.blog.utils.AK
import github.ijkzen.blog.utils.ASSETS_DIR
import github.ijkzen.blog.utils.BUCKET
import github.ijkzen.blog.utils.SK
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.util.*

/**
 * @Author ijkzen
 * @Date 2019/9/26
 */
@Service
class OSSService {

    @Autowired
    private lateinit var ossRepository: OSSRepository

    private var imageList = LinkedList<File>()

    companion object {

        //todo get ak and sk from db
        fun getUploadToken(key: String): String = Auth.create(AK, SK).uploadToken(BUCKET, key)

        var uploadManager: UploadManager? = null
            get() {
                if (field == null) {
                    field = UploadManager(Configuration(Region.region0()))
                }
                return field
            }
    }

    // fileName: images/2019/09/27/xxx.png
    fun uploadFile(fileName: String): Boolean {
        val key = fileName.replace("\\", "/")
        val file = File("$ASSETS_DIR/$key")
        val response = uploadManager!!.put(file, key, getUploadToken(key))
        val result = ObjectMapper().readValue<DefaultPutRet>(response.bodyString())
        return key == result.key
    }

    fun uploadAllImages() {
        val imagesDir = File("$ASSETS_DIR/images/")
        if (imagesDir.exists()) {
            val list = getAllImages(imagesDir)
            list.forEach {
                uploadFile(it.absolutePath.substring(it.absolutePath.indexOf("images")))
            }
            imageList = LinkedList()
        } else {
            System.err.println("images dir not exist")
        }
    }

    private fun getAllImages(file: File): List<File> {

        if (file.isFile) {
            imageList.add(file)
        } else {
            file.listFiles()?.forEach {
                getAllImages(it)
            }
        }
        return imageList
    }

    fun getOssInUse(): OSS? {
        val list = ossRepository.findByInUseIsTrue()
        return if (list == null || list.isEmpty()) {
            null
        } else {
            list[0]
        }
    }
}
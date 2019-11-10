package github.ijkzen.blog.service.oos

import github.ijkzen.blog.utils.ASSETS_DIR
import java.io.File
import java.util.*

abstract class AbstractOSS {

    private val imageList = LinkedList<File>()

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

    abstract fun uploadFile(fileName: String)

    fun uploadAllImages() {
        val imagesDir = File("$ASSETS_DIR/images/")
        if (imagesDir.exists()) {
            val list = getAllImages(imagesDir)
            list.forEach {
                uploadFile(it.absolutePath.substring(it.absolutePath.indexOf("images")))
            }
            imageList.clear()
        } else {
            System.err.println("images dir not exist")
        }
    }
}
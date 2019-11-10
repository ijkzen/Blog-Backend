package github.ijkzen.blog.controller

import github.ijkzen.blog.utils.IMAGES_DIR
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.io.File
import java.net.URL

@Api(
    value = "图片",
    tags = ["CDN"],
    description = "文章内图片"
)
@RestController
@RequestMapping("/images")
class ImageController {


    @ApiOperation(
        value = "获取图片资源",
        notes = """
            只有当CDN没有被配置时才会使用此API
        """
    )
    @ResponseBody
    @GetMapping(value = ["/{year}/{month}/{day}/{fileName}"], produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getImage(
        @PathVariable("year") year: String,
        @PathVariable("month") month: String,
        @PathVariable("day") day: String,
        @PathVariable("fileName") fileName: String
    ): ByteArray {
        val image = File("$IMAGES_DIR/$year/$month/$day/$fileName")
        return if (image.exists()) {
            image.readBytes()
        } else {
            URL("https://cdn.nextto.top/no_pic.png").readBytes()
        }
    }
}
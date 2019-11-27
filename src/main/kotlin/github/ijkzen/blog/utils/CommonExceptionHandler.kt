package github.ijkzen.blog.utils

import github.ijkzen.blog.bean.BaseBean
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
@ResponseBody
class CommonExceptionHandler {

    @ExceptionHandler
    fun commonException(e: Exception): BaseBean {
        return BaseBean().apply {
            errCode = "500"
            errMessage = e.localizedMessage
        }
    }
}
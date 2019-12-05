package github.ijkzen.blog.utils

import github.ijkzen.blog.bean.BaseBean
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.server.ServerErrorException
import org.springframework.web.servlet.NoHandlerFoundException

@ControllerAdvice
@ResponseBody
class CommonExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException::class)
    fun notFoundExceptionHandler(): BaseBean {
        return BaseBean().apply {
            errCode = "404"
            errMessage = "resource not found"
        }
    }

    @ExceptionHandler(ServerErrorException::class)
    fun serverErrorExceptionHandler(): BaseBean {
        return BaseBean().apply {
            errCode = "500"
            errMessage = "server occurred error"
        }
    }
}
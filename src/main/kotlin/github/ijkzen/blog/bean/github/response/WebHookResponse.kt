package github.ijkzen.blog.bean.github.response

import github.ijkzen.blog.bean.github.request.WebHookConfig

data class WebHookResponse(
    var type: String? = null,
    var id: Long? = null,
    var name: String = "web",
    var config: WebHookConfig? = null
)
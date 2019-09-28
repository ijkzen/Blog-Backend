package github.ijkzen.blog.bean

import github.ijkzen.blog.annotation.DefaultOpen

@DefaultOpen
class BaseBean(
        var errCode: String? = "000",
        var errMessage: String? = ""
)
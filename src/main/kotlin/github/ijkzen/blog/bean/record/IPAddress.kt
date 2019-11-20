package github.ijkzen.blog.bean.record

import github.ijkzen.blog.annotation.DefaultOpen

@DefaultOpen
data class IPAddress(
    var country: String,
    var regionName: String
)


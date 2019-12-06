package github.ijkzen.blog.bean.record

import com.fasterxml.jackson.annotation.JsonProperty

data class IPAddress(
    var country: String,
    @JsonProperty("regionName")
    var region: String,
    var city: String
)


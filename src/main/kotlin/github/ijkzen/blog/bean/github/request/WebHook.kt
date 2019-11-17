package github.ijkzen.blog.bean.github.request

import com.fasterxml.jackson.annotation.JsonProperty

data class WebHook(
    @JsonProperty("name")
    var name: String? = null,
    @JsonProperty("events")
    var events: List<String>? = null,
    @JsonProperty("active")
    var active: Boolean = true,
    @JsonProperty("config")
    var config: WebHookConfig? = null
)
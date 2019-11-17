package github.ijkzen.blog.bean.github.request

import com.fasterxml.jackson.annotation.JsonProperty

data class WebHookConfig(
    @JsonProperty("url")
    var url: String = "",
    @JsonProperty("content_type")
    var contentType: String = "",
    @JsonProperty("secret")
    var secret: String? = null,
    @JsonProperty("insecure_ssl")
    var insecureSsl: String = "0"
)
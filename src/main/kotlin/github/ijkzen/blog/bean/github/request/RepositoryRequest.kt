package github.ijkzen.blog.bean.github.request

import com.fasterxml.jackson.annotation.JsonProperty

data class RepositoryRequest(
        @JsonProperty("name")
        var name: String?,

        @JsonProperty("private")
        var `private`: Boolean = false,

        @JsonProperty("auto_init")
        var autoInit: Boolean = true,

        @JsonProperty("license_template")
        var licenseTemplate: String? = "mit"
)
package github.ijkzen.blog.bean.github.response

import com.fasterxml.jackson.annotation.JsonProperty

data class GithubToken(
        @JsonProperty("access_token")
        val accessToken: String,

        @JsonProperty("token_type")
        val tokenType: String,

        @JsonProperty("scope")
        val scope: String

)
package github.ijkzen.blog.bean

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class GithubToken(
        @JsonProperty("access_token")
        val accessToken: String,

        @JsonProperty("token_type")
        val tokenType: String,

        @JsonProperty("scope")
        val scope: String

): Serializable
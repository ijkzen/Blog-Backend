package github.ijkzen.blog.bean.github.response

import com.fasterxml.jackson.annotation.JsonProperty

// GithubTokenBean(
// accessToken=1e64c0158a5b0e9d55a491fafc56816847e7cee3,
// tokenType=bearer,
// scope=public_repo,user:email
// )

data class GithubTokenBean(
        @JsonProperty("access_token")
        val accessToken: String,

        @JsonProperty("token_type")
        val tokenType: String,

        @JsonProperty("scope")
        val scope: String

)
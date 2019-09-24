package github.ijkzen.blog.bean.github.response

data class GithubEmail(
        val email: String?,
        val primary: Boolean,
        val verified: Boolean
)
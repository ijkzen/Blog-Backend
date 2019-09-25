package github.ijkzen.blog.bean.github.response

data class GithubEmailBean(
        val email: String?,
        val primary: Boolean,
        val verified: Boolean
)
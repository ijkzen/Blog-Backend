package github.ijkzen.blog.bean.github.response

// GithubEmailBean(
// email=krystalandhola@outlook.com,
// primary=true,
// verified=true
// )

data class GithubEmailBean(
        val email: String?,
        val primary: Boolean,
        val verified: Boolean
)
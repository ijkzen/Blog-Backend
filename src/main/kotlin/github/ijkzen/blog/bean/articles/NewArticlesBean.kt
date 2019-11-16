package github.ijkzen.blog.bean.articles

import github.ijkzen.blog.bean.BaseBean

data class NewArticlesBean(
    var list: List<NewArticle>? = null
) : BaseBean()
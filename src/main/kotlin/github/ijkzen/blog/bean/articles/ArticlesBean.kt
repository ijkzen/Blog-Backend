package github.ijkzen.blog.bean.articles

import github.ijkzen.blog.bean.BaseBean

data class ArticlesBean(
        var list: List<Article>?
) : BaseBean()
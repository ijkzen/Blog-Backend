package github.ijkzen.blog.bean.category

import github.ijkzen.blog.bean.BaseBean

data class CategoryBean(
        var list: List<Category>? = null
) : BaseBean()
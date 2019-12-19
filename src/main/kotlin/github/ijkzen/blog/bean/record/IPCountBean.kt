package github.ijkzen.blog.bean.record

import github.ijkzen.blog.bean.BaseBean

data class IPCountBean(
    var country: String? = null,
    var region: String? = null,
    var city: String? = null,
    var size: Long? = 0
)

data class IPCountsBean(
    var list: List<IPCountBean>
) : BaseBean()

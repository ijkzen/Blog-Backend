package github.ijkzen.blog.bean.github.response

import com.fasterxml.jackson.annotation.JsonProperty

data class RepositoryRequest(
        @JsonProperty("id")
        var id: Long?,

        @JsonProperty("node_id")
        var nodeId: String?,

        @JsonProperty("name")
        var name: String?,

        @JsonProperty("full_name")
        var fullName: String?,

        @JsonProperty("language")
        var language: String?,

        @JsonProperty("watchers_count")
        var watchersCount: Int?,

        @JsonProperty("size")
        var size: Int?,

        @JsonProperty("default_branch")
        var defaultBranch: String?,

        @JsonProperty("created_at")
        var createdTime: String?,

        @JsonProperty("updated_at")
        var updatedTime: String?
) {
    constructor() : this(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
    )
}
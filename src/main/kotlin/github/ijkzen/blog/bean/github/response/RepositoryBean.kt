package github.ijkzen.blog.bean.github.response

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "Repository")
data class RepositoryBean(
        @Id
        @JsonProperty("id")
        var id: Long?,

        @Column
        @JsonProperty("node_id")
        var nodeId: String?,

        @Column
        @JsonProperty("name")
        var name: String?,

        @Column
        @JsonProperty("full_name")
        var fullName: String?,

        @Column
        @JsonProperty("html_url")
        var htmlUrl: String?,

        @Column
        @JsonProperty("language")
        var language: String?,

        @Column
        @JsonProperty("watchers_count")
        var watchersCount: Int?,

        @Column
        @JsonProperty("size")
        var size: Int?,

        @Column
        @JsonProperty("default_branch")
        var defaultBranch: String?,

        @Column
        @JsonProperty("created_at")
        var createdTime: String?,

        @Column
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
            null,
            null
    )
}
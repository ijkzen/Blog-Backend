package github.ijkzen.blog.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Developer(
        @JsonProperty("login")
        var developerName: String?,

        @JsonProperty("id")
        var developerId: Long,

        @JsonProperty("node_id")
        var nodeId: String?,

        @JsonProperty("avatar_url")
        var avatarUrl: String?,

        @JsonProperty("gravatar_id")
        var gravatarId: String?,

        @JsonProperty("url")
        var url: String?,

        @JsonProperty("html_url")
        var htmlUrl: String?,

        @JsonProperty("followers_url")
        var followersUrl: String?,

        @JsonProperty("following_url")
        var following_url: String?,

        @JsonProperty("gists_url")
        var gistsUrl: String?,

        @JsonProperty("starred_url")
        var starred_url:String?,

        @JsonProperty("subscriptions_url")
        var subscriptionsUrl: String?,

        @JsonProperty("organizations_url")
        var organizationsUrl: String?,

        @JsonProperty("repos_url")
        var reposUrl: String?,

        @JsonProperty("events_url")
        var eventsUrl: String?,

        @JsonProperty("received_events_url")
        var receivedEventsUrl: String?,

        @JsonProperty("type")
        var type: String?,

        @JsonProperty("site_admin")
        var siteAdmin: Boolean,

        @JsonProperty("name")
        var name: String?,

        @JsonProperty("company")
        var company: String?,

        @JsonProperty("blog")
        var blog: String?,

        @JsonProperty("location")
        var location: String?,

        @JsonProperty("email")
        var email: String?,

        @JsonProperty("hireable")
        var hireable: String?,

        @JsonProperty("bio")
        var bio: String?,

        @JsonProperty("public_repos")
        var publicRepos: Int,

        @JsonProperty("public_gists")
        var publicGists: Int,

        @JsonProperty("followers")
        var followers: Int,

        @JsonIgnore
        var state: String?,

        @JsonIgnore
        var token: String?
)
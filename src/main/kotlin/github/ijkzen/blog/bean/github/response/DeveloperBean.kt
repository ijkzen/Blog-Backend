package github.ijkzen.blog.bean.github.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

//Developer(
// developerName=ijkzen,
// developerId=31531836,
// nodeId=MDQ6VXNlcjMxNTMxODM2,
// avatarUrl=https://avatars2.githubusercontent.com/u/31531836?v=4,
// gravatarId=, url=https://api.github.com/users/ijkzen,
// htmlUrl=https://github.com/ijkzen,
// followersUrl=https://api.github.com/users/ijkzen/followers,
// followingUrl=https://api.github.com/users/ijkzen/following{/other_user},
// gistsUrl=https://api.github.com/users/ijkzen/gists{/gist_id},
// starredUrl=https://api.github.com/users/ijkzen/starred{/owner}{/repo},
// subscriptionsUrl=https://api.github.com/users/ijkzen/subscriptions,
// organizationsUrl=https://api.github.com/users/ijkzen/orgs,
// reposUrl=https://api.github.com/users/ijkzen/repos,
// eventsUrl=https://api.github.com/users/ijkzen/events{/privacy},
// receivedEventsUrl=https://api.github.com/users/ijkzen/received_events,
// type=User,
// siteAdmin=false,
// name=IJKZEN,
// company=null,
// blog=https://ijkzen.github.io,
// location=China,
// email=ijkzen@outlook.com,
// hireable=null,
// bio=An android developer,
// publicRepos=12,
// publicGists=0,
// followers=1,
// state=null,
// token=22bc5f7b03439b4dd3a9f29c9cf0ffce09b27b6f
// )

@Entity(name = "Developer")
data class DeveloperBean(
        @Column
        @JsonProperty("login")
        var developerName: String?,

        @Id
        @JsonProperty("id")
        var developerId: Long?,

        @Column
        @JsonProperty("node_id")
        var nodeId: String?,

        @Column
        @JsonProperty("avatar_url")
        var avatarUrl: String?,

        @Column
        @JsonProperty("gravatar_id")
        var gravatarId: String?,

        @Column
        @JsonProperty("url")
        var url: String?,

        @Column
        @JsonProperty("html_url")
        var htmlUrl: String?,

        @Column
        @JsonProperty("followers_url")
        var followersUrl: String?,

        @Column
        @JsonProperty("following_url")
        var followingUrl: String?,

        @Column
        @JsonProperty("gists_url")
        var gistsUrl: String?,

        @Column
        @JsonProperty("starred_url")
        var starredUrl: String?,

        @Column
        @JsonProperty("subscriptions_url")
        var subscriptionsUrl: String?,

        @Column
        @JsonProperty("organizations_url")
        var organizationsUrl: String?,

        @Column
        @JsonProperty("repos_url")
        var reposUrl: String?,

        @Column
        @JsonProperty("events_url")
        var eventsUrl: String?,

        @Column
        @JsonProperty("received_events_url")
        var receivedEventsUrl: String?,

        @Column
        @JsonProperty("type")
        var type: String?,

        @Column
        @JsonProperty("site_admin")
        var siteAdmin: Boolean?,

        @Column
        @JsonProperty("name")
        var name: String?,

        @Column
        @JsonProperty("company")
        var company: String?,

        @Column
        @JsonProperty("blog")
        var blog: String?,

        @Column
        @JsonProperty("location")
        var location: String?,

        @Column
        @JsonProperty("email")
        var email: String?,

        @Column
        @JsonProperty("hireable")
        var hireable: String?,

        @Column
        @JsonProperty("bio")
        var bio: String?,

        @Column
        @JsonProperty("public_repos")
        var publicRepos: Int?,

        @Column
        @JsonProperty("public_gists")
        var publicGists: Int?,

        @Column
        @JsonProperty("followers")
        var followers: Int?,

        @Column
        @JsonIgnore
        var state: String?,

        @Column
        @JsonIgnore
        var token: String?
) {
    constructor() :
            this(
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
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null) {

    }
}
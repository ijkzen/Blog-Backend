package github.ijkzen.blog.controller

import github.ijkzen.blog.bean.Developer
import github.ijkzen.blog.bean.GithubEmail
import github.ijkzen.blog.bean.GithubToken
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.util.*

const val CLIENT_ID = "280efb2391f54e4992d7"
const val CLIENT_SECRET = "f7692a0d1bc128d27203feacc26d5143199a19b5"
val restTemplate = RestTemplate()

@RestController
class OAuthController {

    @GetMapping(value = ["/oauth/github"])
    fun getToken(@RequestParam("code") code: String) {
        val token = restTemplate.getForObject(
                "https://github.com/login/oauth/access_token?" +
                        "client_id=$CLIENT_ID" +
                        "&client_secret=$CLIENT_SECRET" +
                        "&code=${code}",
                GithubToken::class.java
        )

        getDeveloperInfo(token!!.accessToken)
    }

    private fun getDeveloperInfo(token: String) {
        val headers = HttpHeaders()
        headers.accept = Collections.singletonList(MediaType.APPLICATION_JSON)
        headers.set("Authorization", "token $token")
        val entity = HttpEntity("body", headers)
        val result = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                entity,
                Developer::class.java
        )

        val developer = result.body!!
        developer.token = token

        if (developer.email.isNullOrEmpty()) {
            getDeveloperEmail(developer)
        }
    }

    private fun getDeveloperEmail(developer: Developer) {
        val headers = HttpHeaders()
        headers.accept = Collections.singletonList(MediaType.APPLICATION_JSON)
        headers.set("Authorization", "token ${developer.token}")
        val entity = HttpEntity("body", headers)
        val email = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                entity,
                Array<GithubEmail>::class.java
        )

        developer.email = email.body!!.find { it.primary }!!.email

        System.err.println(developer)
    }

}
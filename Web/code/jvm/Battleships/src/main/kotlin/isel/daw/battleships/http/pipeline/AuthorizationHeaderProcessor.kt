package isel.daw.battleships.http.pipeline


import isel.daw.battleships.controllers.model.User
import isel.daw.battleships.services.ServicesPlayers
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.HashMap

@Component
class AuthorizationHeaderProcessor(
    val usersService: ServicesPlayers
) {

    fun process(authorizationValue: String?): User? {
        if (authorizationValue == null) {
            return null
        }
        val parts = authorizationValue.trim().split(" ")
        if (parts.size != 2) {
            return null
        }
        if (parts[0].lowercase() != SCHEME_BEARER) {
            return null
        }
        val name = usersService.checkBearerToken(parts[1])?: return null
        return User(name)
    }

        fun processCookie(authorizationValue: String?): User? {
            if (authorizationValue == null) {
                return null
            }
            val cookies = HashMap<String, String>()
            authorizationValue.split("; ").forEach { cookie ->
                val index = cookie.indexOf('=')
                val name = cookie.substring(0, index)
                val value = cookie.substring(index + 1)
                cookies[name] = value
            }
            val tokenCookie = cookies["token"] ?: return null
            val name = usersService.checkBearerToken(tokenCookie)?: return null
            return User(name)
        }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthorizationHeaderProcessor::class.java)
        const val SCHEME_BEARER = "bearer"
    }
}

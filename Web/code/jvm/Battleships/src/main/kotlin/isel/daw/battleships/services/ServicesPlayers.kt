package isel.daw.battleships.services


import isel.daw.battleships.ExceptionControllerAdvice
import isel.daw.battleships.database.jdbi.TransactionManager
import isel.daw.battleships.model.Player
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.MessageDigest

@Component
class ServicesPlayers(private val transactionManager: TransactionManager) : ServicesPlayersInterface {

    override fun getPlayerByUsername(username: String): Player {
        if (username.isBlank())
            throw ExceptionControllerAdvice.CantSearchForBlankPlayer("Username can't be blank.")
        if (username.length > 32)
            throw ExceptionControllerAdvice.InvalidParameterException("Username length can't be bigger than 32chars.")
        val player = transactionManager.run {
            val usersRepository = it.usersRepository
            usersRepository.getPlayerByUsername(username)
        } ?: throw ExceptionControllerAdvice.PlayerNotFoundException("This username doesn't exist.")
        return player
    }

    override fun getRankings(pages: Int?): List<Player> {
        val total = pages ?: 10
        if (total < 0) throw ExceptionControllerAdvice.RankingPagesCannotBeNegative("Number of pages can't be negative.")
        return transactionManager.run {
            val usersRepository = it.usersRepository
            usersRepository.getRankings(total)
        }
    }

    override fun createPlayer(name: String, password: String): String {
        if (name.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Username can't be blank.")
        if (password.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Password can't be blank.")
        if (name.length > 32)
            throw ExceptionControllerAdvice.InvalidParameterException("Username length can't be bigger than 32chars.")
        if (password.length > 32)
            throw ExceptionControllerAdvice.InvalidParameterException("Password length can't be bigger than 32chars.")
        if (transactionManager.run {
                val usersRepository = it.usersRepository
                usersRepository.getPlayerByUsername(name)
            } != null) throw ExceptionControllerAdvice.AlreadyExistingDataException("User Already Exists.")
        return transactionManager.run {
            val usersRepository = it.usersRepository
            usersRepository.postPlayer(name, password.md5())
        }
    }

    override fun checkBearerToken(bearerToken: String): String? = transactionManager.run {
        val usersRepository = it.usersRepository
        usersRepository.checkBearerToken(bearerToken)
    }

    override fun authPlayer(username: String, hashPassword: String): String {
        if (username.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Username can't be blank.")
        if (hashPassword.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Password can't be blank.")
        if (username.length > 32)
            throw ExceptionControllerAdvice.InvalidParameterException("Username length can't be bigger than 32chars.")
        if (hashPassword.length > 32)
            throw ExceptionControllerAdvice.InvalidParameterException("Password length can't be bigger than 32chars.")

        return transactionManager.run {
            val usersRepository = it.usersRepository
            usersRepository.authPlayer(username, hashPassword.md5())
        } ?: throw ExceptionControllerAdvice.FailedAuthenticationException("Incorrect Username Or Password.")

    }
}

/**
 * Generates an MD5 Hash for a certain password
 */
fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

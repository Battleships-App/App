package isel.daw.battleships.database.jdbi

import isel.daw.battleships.database.UsersRepository
import isel.daw.battleships.model.Player
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.Handle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.*

class JdbiUsersRepository(private val handle: Handle) : UsersRepository {
    override fun getRankings(pages: Int): List<Player> =
        handle.createQuery("select username,uuid,hashpassword,games,points from player order by points DESC fetch first $pages rows only")
            .mapTo(Player::class.java)
            .list()


    override fun getPlayerByUsername(username: String): Player? =
        handle.createQuery("select username,uuid,hashpassword,games,points from player where username = :username")
            .bind("username", username)
            .mapTo(Player::class.java)
            .singleOrNull()


    override fun checkBearerToken(bearerToken: String): String? =
        handle.createQuery("select username from player where uuid = :uuid")
            .bind("uuid", bearerToken)
            .mapTo(String::class.java)
            .singleOrNull()

    override fun authPlayer(username: String, hashPassword: String): String? {
        val player =
            handle.createQuery("select username,uuid,hashpassword,games,points from player where username = :username and hashpassword = :hashpassword")
                .bind("username", username)
                .bind("hashpassword", hashPassword)
                .mapTo(Player::class.java)
                .singleOrNull() ?: return null
        return player.uuid
    }

    override fun postPlayer(username: String, hashPassword: String): String {
        val newUUID = UUID.randomUUID().toString()
            handle.createUpdate(
                "insert into player(username,uuid, hashpassword, games, points) values (:username,:uuid,:hashpassword,:games ,:points)"
            )
                .bind("username", username)
                .bind("uuid", newUUID)
                .bind("hashpassword", hashPassword)
                .bind("games", 0)
                .bind("points", 0)
                .execute()
        return newUUID
    }
}

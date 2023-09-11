package isel.daw.battleships.services

import isel.daw.battleships.model.Player

interface ServicesPlayersInterface {

    /**-------------------------------Player--------------------------------------**/

    /**
     * Gets a Player by his [username].
     * @return correspondent Player Object
     */
    fun getPlayerByUsername(username: String): Player?

    /**
     * Gets the LeaderBoard list ordered by points.
     * @return [Player] list
     */
    fun getRankings(pages: Int?): List<Player>

    /**
     * Creates a new Player with [name] and [password].
     * @return UUID of newly created player
     */
    fun createPlayer(name: String, password: String): String

    /**
     * Gets username of given [bearerToken] or NULL if there's none.
     * @return username
     */
    fun checkBearerToken(bearerToken: String): String?


    /**
     * Authenticates [username] credentials with given [hashPassword].
     * @return UUID of [username] if credentials match or NULL if they don't
     */
    fun authPlayer(username: String, hashPassword: String): String?

}


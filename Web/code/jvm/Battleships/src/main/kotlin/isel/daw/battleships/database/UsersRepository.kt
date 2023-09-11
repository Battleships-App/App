package isel.daw.battleships.database

import isel.daw.battleships.model.Player

interface UsersRepository{

    /**
     * Retrieves list of Players from Database ordered by Points.
     * @return List of Players.
     */
    fun getRankings(pages: Int): List<Player>

    /**
     * Retrieves data from [username] from the Database.
     * @return the username data if in Database, if not returns NULL.
     */
    fun getPlayerByUsername(username: String): Player?

    /**
     * Checks if [bearerToken] is with player in the Database.
     * @return TRUE if information checks, FALSE if not.
     */
    fun checkBearerToken(bearerToken: String): String?

    /**
     * Checks if [username] and [hashPassword] check with what is in the Database.
     * @return TRUE if information checks, FALSE if not.
     */
    fun authPlayer(username: String, hashPassword: String): String?

    /**
     * Posts a new player in the Database with given [username] and [hashPassword].
     * @return TRUE if the player was added, FALSE if username already exists.
     */
    fun postPlayer(username: String, hashPassword: String): String

}

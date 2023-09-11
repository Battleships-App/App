package isel.daw.battleships.services

import isel.daw.battleships.model.Game

interface ServicesGameInterface {

    /**--------------------------------Game---------------------------------------**/

    /**
     * Gets all games in DB with State in WAITING, which means the player is Waiting for another one to join
     * @return list of games or NULL if the there's none.
     */
    fun getAllOpenGames(): List<Game>?

    /**
     * Gets all games in DB with State in WAITING and created by the [username], which means the player is Waiting for another one to join
     * @return list of games or NULL if the there's none.
     */
    fun getOpenGamesByPlayer(username: String): List<Game>?

    /**
     * Gets all games in DB with State in BUILDING or STARTED and created/joined by the [username], which means the games are running
     * @return list of games or NULL if the there's none.
     */
    fun getRunningGamesByPlayer(username: String): List<Game>?

    /**
     * Gets all games in DB with State in ENDED, which means these games were the ones last played by given [username]
     * @return list of games or NULL if the there's none.
     */
    fun getLastGamesByPlayer(username: String): List<Game>?

    /**
     * Gets game in DB given by [gameId] and evaluates if [username] can join
     * @return [Game] if user has joined or NULL if he didn't.
     */
    fun joinGame(gameId: String, username: String): Game?

    /**
     * Creates game in DB for [username] with given [shotsPerRound] rule
     * @return UUID of game if game was created or NULL if it wasn't.
     */
    fun postGame(username: String, shotsPerRound: Int): String

    /**
     * Goes to given game given by [gameId] and [username] and retrieves [username] last moves.
     * @return the [String] with track of Moves
     */
    fun getLastMoves(gameId: String, username: String): String?

    /**
     * Goes to given game given by [gameId] and [username] and retrieves [username] enemy's last moves.
     * @return the [String] with track of Moves
     */
    fun getLastMovesOfEnemyPlayer(gameId: String, username: String): String?

    /**
     * Goes to game given by [gameId] and checks its STATE.
     * @return the STATE of game.
     */
    fun getGameStatus(gameId: String): String

    /**
     * Goes to board given the [gameId] and [username] and analyses enemy board to get fleet information.
     * @return ListOf[String] with given Fleet Information, if ships are Alive or Sunken
     */
    fun getMyFleetStatus(gameId: String, username: String) : MutableList<String>

    /**
     * Goes to enemy board given the [gameId] and [username] and analyses enemy board to get fleet information.
     * @return ListOf[String] with given Fleet Information, if ships are Alive or Sunken
     */
    fun getEnemyFleetStatus(gameId: String, username: String) :  MutableList<String>


    fun forfeitGame(gameId: String, username: String): String
}

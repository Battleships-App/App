package isel.daw.battleships.database

import isel.daw.battleships.model.Game

interface GamesRepository {

    /**
     * Gets a Game by its ID
     * @return game object if it exists
     */
    fun getGame(gameId: String): Game?

    fun joinGame(gameId: String, username: String): Game

    /**
     * Creates a new game in the Database for given [username].
     * @return UUID of game object if operation successful and NULL if not.
     */
    fun createGame(username: String, shotsPerRound: Int): String?


    /**
     * Gets all open games (games in which the State is WAITING) from the Database.
     * @return list of games or NULL if the there's none.
     */
    fun getAllOpenGames(): List<Game>

    /**
     * Gets all open games by given [username] (games in which the State is WAITING) from the Database.
     * @return list of games or NULL if the there's none.
     */
    fun getOpenGamesByPlayer(username: String): List<Game>


    fun getRunningGamesByPlayer(username: String): List<Game>
    /**
     * Gets all previous games by given [username] (games in which the State is ENDED) from the Database.
     * @return list of games or NULL if the there's none.
     */
    fun getLastGamesByPlayer(username: String): List<Game>


    /**
     * Checks the Board of given [gameId] and [playerId] and returns list of [playerId] previous moves.
     * @return List of Square or NULL if there's no previous moves
     */
    fun getLastMoves(gameId: String, playerId: Int): String


    /**
     * Gets the fleet of the
     */
    fun getGameBoards(gameId: String): Pair<String, String>

    /**
     * Fetches the corresponding Player's board
     */
    fun getPlayerBoard(gameId: String, player: Int): String

    fun getPlayerId(gameId: String, username: String): Int?
    fun updatePlayerMoves(gameId: String, playerId: Int, moves: String): Boolean
    fun updatePlayerBoard(gameId: String, playerId: Int, playerBoardString: String): Boolean
    fun endGame(gameId: String, playerId: Int)
    fun startGame(gameId : String)
}


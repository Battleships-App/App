package isel.daw.battleships.database.jdbi

import isel.daw.battleships.database.GamesRepository
import isel.daw.battleships.model.Game
import org.jdbi.v3.core.Handle
import org.springframework.stereotype.Repository
import java.sql.SQLException
import java.util.*

class JdbiGamesRepository(private val handle: Handle) : GamesRepository {

    override fun getGame(gameId: String): Game? = handle.createQuery("select * from game where uuid = :uuid")
        .bind("uuid", gameId)
        .mapTo(Game::class.java)
        .singleOrNull()


    override fun joinGame(gameId: String, username: String): Game {
        handle.createUpdate("update player set games=games+1 where username=:username")
            .bind("username", username)
            .execute()

        return handle.createQuery(
            "update game set player2Username=:player2Username, gameState='BUILDING', nextPlay='PLAYER1'," + "board1='________________________________________________________________'," + "board2='________________________________________________________________' " + "where uuid=:uuid and gameState='WAITING' " + "returning uuid,player1Username,player2Username,gameState,nextPlay,board1,board2,moves1,moves2,shotsPerRound,result"
        )
            .bind("player2Username", username)
            .bind("uuid", gameId)
            .mapTo(Game::class.java)
            .single()
    }

    override fun createGame(username: String, shotsPerRound: Int): String? {
        val newUUID = UUID.randomUUID().toString()
        handle.createUpdate(
            "insert into game(uuid,player1Username,player2Username,gameState,nextPlay,board1,board2,moves1,moves2,shotsPerRound,result) values (:uuid,:player1Username,:player2Username,:gameState,:nextPlay,:board1,:board2,:moves1,:moves2,:shotsPerRound,:result)"
        )
            .bind("uuid", newUUID)
            .bind("player1Username", username)
            .bind("player2Username", "")
            .bind("gameState", "WAITING")
            .bind("nextPlay", "PLAYER1")
            .bind("board1", "")
            .bind("board2", "")
            .bind("moves1", "")
            .bind("moves2", "")
            .bind("shotsPerRound", shotsPerRound)
            .bind("result", 0)
            .execute()

        handle.createUpdate("update player set games=games+1 where username=:username")
            .bind("username", username)
            .execute()
        return newUUID
    }


    override fun getAllOpenGames(): List<Game> =
        handle.createQuery("select * from game where gameState='WAITING'").mapTo(Game::class.java).list()


    override fun getOpenGamesByPlayer(username: String): List<Game> =
        handle.createQuery("select * from game where (player1Username=:player1Username or player2Username=:player2Username) and gameState='WAITING'")
            .bind("player1Username", username)
            .bind("player2Username", username)
            .mapTo(Game::class.java).list()

    override fun getRunningGamesByPlayer(username: String): List<Game> =
        handle.createQuery("select * from game where (player1Username=:player1Username or player2Username=:player2Username) and (gameState='BUILDING' or gameState='STARTED')")
            .bind("player1Username", username)
            .bind("player2Username", username)
            .mapTo(Game::class.java)
            .list()


    override fun getLastGamesByPlayer(username: String): List<Game> =
        handle.createQuery("select * from game where (player1Username=:player1Username or player2Username=:player2Username) and gameState='ENDED'")
            .bind("player1Username", username)
            .bind("player2Username", username)
            .mapTo(Game::class.java)
            .list()


    override fun getLastMoves(gameId: String, playerId: Int): String =
        handle.createQuery("select moves$playerId from game where uuid=:uuid")
            .bind("uuid", gameId)
            .mapTo(String::class.java)
            .single()

    //Não deve ir buscar tudo
    override fun getGameBoards(gameId: String): Pair<String, String> {
        val game = handle.createQuery("select * from game where uuid=:uuid")
            .bind("uuid", gameId)
            .mapTo(Game::class.java).single()

        return Pair(game.board1, game.board2)
    }

    override fun getPlayerId(gameId: String, username: String): Int? {
        val player1 = handle.createQuery("select player1Username from game where uuid=:uuid")
            .bind("uuid", gameId)
            .mapTo(String::class.java)
            .singleOrNull() ?: return null
        return if (username == player1) 1 else 2
    }

    override fun getPlayerBoard(gameId: String, player: Int): String =
        handle.createQuery("select board$player from game where uuid=:uuid")
            .bind("uuid", gameId)
            .mapTo(String::class.java)
            .single()

    override fun updatePlayerMoves(gameId: String, playerId: Int, moves: String): Boolean {
        val nextPlayerId = if (playerId == 1) 2 else 1
        val moveUpdatedReturnedFromDB =
            handle.createQuery("update game set moves$playerId=:moves$playerId, nextPlay=:nextPlay where uuid=:uuid returning moves$playerId")
                .bind("nextPlay", "PLAYER$nextPlayerId")
                .bind("uuid", gameId)
                .bind("moves$playerId", moves)
                .mapTo(String::class.java)
                .singleOrNull()
        return moveUpdatedReturnedFromDB == moves
    }

    override fun updatePlayerBoard(gameId: String, playerId: Int, playerBoardString: String): Boolean {
        val boardUpdatedReturnedFromDB =
            handle.createQuery("update game set board$playerId=:board$playerId where uuid=:uuid returning board$playerId")
                .bind("board$playerId", playerBoardString)
                .bind("uuid", gameId)
                .mapTo(String::class.java).singleOrNull()
        return boardUpdatedReturnedFromDB == playerBoardString
    }

    override fun endGame(gameId: String, playerId: Int) {
        val playerString = "player$playerId" + "Username"
        val usernameWinner =
            handle.createQuery("update game set result=:result, gameState='ENDED' where uuid=:uuid returning $playerString")
                .bind("result", playerId)
                .bind("uuid", gameId)
                .mapTo(String::class.java).singleOrNull()
        handle.createUpdate("update player set points=points+1 where username=:username")
            .bind("username", usernameWinner)
            .execute()
    }

    override fun startGame(gameId: String) {
        handle.createQuery("update game set gameState='STARTED' where uuid=:uuid returning uuid")
            .bind("uuid", gameId)
            .mapTo(String::class.java)
            .singleOrNull()
    }
}


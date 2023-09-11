package isel.daw.battleships.services

import isel.daw.battleships.ExceptionControllerAdvice
import isel.daw.battleships.database.jdbi.TransactionManager
import isel.daw.battleships.model.*
import org.springframework.stereotype.Component

@Component
class ServicesGame(private val transactionManager: TransactionManager) : ServicesGameInterface {

    /**--------------------------------Game---------------------------------------**/
    override fun getAllOpenGames(): List<Game> {
        val list = transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.getAllOpenGames()
        }
        if (list.isEmpty()) throw ExceptionControllerAdvice.GameNotFoundException("There are no Open games at the moment.")
        else return list
    }

    override fun getOpenGamesByPlayer(username: String): List<Game> {
        val list = transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.getOpenGamesByPlayer(username)
        }
        if (list.isEmpty()) throw ExceptionControllerAdvice.GameNotFoundException("There are no games for this player at the moment.")
        else return list
    }

    override fun getRunningGamesByPlayer(username: String): List<Game> {
        val list = transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.getRunningGamesByPlayer(username)
        }
        if (list.isEmpty()) throw ExceptionControllerAdvice.GameNotFoundException("There are no running games for this player at the moment.")
        else return list
    }

    override fun getLastGamesByPlayer(username: String): List<Game> {
        val list = transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.getLastGamesByPlayer(username)
        }
        if (list.isEmpty()) throw ExceptionControllerAdvice.GameNotFoundException("This player hasn't got any games yet.")
        else return list
    }

    override fun joinGame(gameId: String, username: String): Game = transactionManager.run {
        val gamesRepository = it.gamesRepository
        val game = gamesRepository.getGame(gameId)
            ?: throw ExceptionControllerAdvice.GameNotFoundException("Introduced Game does not exist.")
        if (game.gameState != State.WAITING)
            throw ExceptionControllerAdvice.WrongGameStateException("This Game has already started.")
        // TODO("If im joined, return usernames' board")
        if (game.player1Username == username || game.player2Username == username) throw ExceptionControllerAdvice.AlreadyExistingDataException(
            "You are already inside the game, no need to join again"
        )
        gamesRepository.joinGame(gameId, username)
    }

    override fun postGame(username: String, shotsPerRound: Int): String {
        if (shotsPerRound !in 0..5) throw ExceptionControllerAdvice.InvalidParameterException("Shots per round is not within rules' values")
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.createGame(username, shotsPerRound)
        } ?: throw ExceptionControllerAdvice.GameCouldNotBeCreatedException("Game Could Not be Created..")
    }

    override fun forfeitGame(gameId: String, username: String): String {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.getGame(gameId)
                ?: throw ExceptionControllerAdvice.GameNotFoundException("Introduced Game does not exist.")
            var playerId = gamesRepository.getPlayerId(gameId, username)
                ?: throw ExceptionControllerAdvice.PlayerNotFoundException("Respective player isn't in this game.")
            if(playerId==1) playerId=2 else playerId=1
            gamesRepository.endGame(gameId, playerId = playerId)
            gameId
        }
    }

    override fun getLastMoves(gameId: String, username: String): String {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.getGame(gameId)
                ?: throw ExceptionControllerAdvice.GameNotFoundException("Introduced Game does not exist.")
            val playerId = gamesRepository.getPlayerId(gameId, username)
                ?: throw ExceptionControllerAdvice.PlayerNotFoundException("Respective player isn't in this game.")
            gamesRepository.getLastMoves(gameId, playerId = playerId)
        }
    }

    override fun getLastMovesOfEnemyPlayer(gameId: String, username: String): String {
        val enemyid = if (getPlayerId(gameId, username) == 1) 2 else 1
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.getLastMoves(gameId, enemyid)
        }
    }

    fun getLastStateOfGame(gameId: String, username: String): Game {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            val game = gamesRepository.getGame(gameId)
                ?: throw ExceptionControllerAdvice.GameNotFoundException("Introduced Game does not exist.")
            gamesRepository.getPlayerId(gameId, username)
                ?: throw ExceptionControllerAdvice.PlayerNotFoundException("Respective player isn't in this game.")
            game
        }
    }

    override fun getGameStatus(gameId: String): String {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            val game = gamesRepository.getGame(gameId)
                ?: throw ExceptionControllerAdvice.GameNotFoundException("Requested Game hasn't been created yet")
            game.gameState.name
        }
    }

    override fun getMyFleetStatus(gameId: String, username: String): MutableList<String> {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            val board = gamesRepository.getGame(gameId)
                ?: throw ExceptionControllerAdvice.GameNotFoundException("Requested Game hasn't been created yet")
            if (board.gameState == State.WAITING || board.gameState == State.BUILDING) throw ExceptionControllerAdvice.WrongGameStateException(
                "No ships placed on board"
            )
            if (board.player1Username != username && board.player2Username != username) throw ExceptionControllerAdvice.UserNotAuthorizedException(
                "You are not playing this game."
            )
            if (username == board.player1Username)
                boardAnalysis(board.board1)
            else
                boardAnalysis(board.board2)
        }
    }

    override fun getEnemyFleetStatus(gameId: String, username: String): MutableList<String> {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            val board = gamesRepository.getGame(gameId)
                ?: throw ExceptionControllerAdvice.GameNotFoundException("Requested Game hasn't been created yet")
            if (board.gameState == State.WAITING || board.gameState == State.BUILDING) throw ExceptionControllerAdvice.WrongGameStateException(
                "No ships placed on board"
            )
            if (board.player1Username != username && board.player2Username != username) throw ExceptionControllerAdvice.UserNotAuthorizedException(
                "You are not playing this game."
            )
            if (username == board.player1Username)
                boardAnalysis(board.board2)
            else
                boardAnalysis(board.board1)
        }
    }

    /**
     * Analyses the board recursively in all positions, ship by ship, and tries to match the current ship char to the current position char,
     * if it matches it increments a ship's hit marker (@param lives), if the hit marker reaches the number of lives the method moves onto the next ship, if all ships have been analysed the
     * method returns.
     * @return The String of ships
     */
    fun boardAnalysis(board: String): MutableList<String> {
        val ships = mutableListOf("Carrier = ", "Battleship = ", "Destroyer = ", "Submarine = ", "Patrol Boat = ")
        val fleet = mutableMapOf('C' to 0, 'B' to 0, 'D' to 0, 'S' to 0, 'P' to 0)
        for (char in board) {
            val charU = char.uppercaseChar()
            if (fleet.keys.contains(charU)) {
                var value = fleet[charU]
                value = value!! + 1
                fleet[charU] = value
            }
        }
        for (i in 0 until fleet.size)
            ships[i] += if (fleet[ships[i][0]]!! != 0) "Alive" else "Sunken"
        return ships
    }

    fun hitOrMiss(gameId: String, username: String, moves: List<String>): List<Boolean> {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            val game: Game = gamesRepository.getGame(gameId)
                ?: throw ExceptionControllerAdvice.GameNotFoundException("Introduced Game does not exist.")
            if (game.gameState == State.ENDED)
                throw ExceptionControllerAdvice.WrongGameStateException("Game has already Ended.")
            if (game.gameState != State.STARTED)
                throw ExceptionControllerAdvice.WrongGameStateException("Game hasn't started yet.") //Jogo ja pode ter acabado, ou ainda em waiting/building.
            if (moves.size != game.shotsPerRound)
                throw ExceptionControllerAdvice.InvalidMovesForSpecifiedShotRules("Moves doesn't match shot rules of the current game.")

            // player1 == AMERICA && player2 == RUSSIA
            val playerId = getPlayerId(gameId, username)
            val enemyPlayerId = if (playerId == 1) 2 else 1
            if (game.nextPlay == Turn.PLAYER1 && playerId != 1 || game.nextPlay == Turn.PLAYER2 && playerId != 2) {
                throw ExceptionControllerAdvice.NotYourTurn("It ain't your turn yet, sit still and wait for other player move..")
            }
            // BLUE == AMERICA && RED == RUSSIA
            val enemyArmy = if (enemyPlayerId == 1) Army.BLUE else Army.RED
            val stringBoard = gamesRepository.getPlayerBoard(gameId, enemyPlayerId)
            val enemyBoard = Board(gameId = gameId, enemyArmy, stringBoard)

            var newMoves = gamesRepository.getLastMoves(gameId, playerId)
            val hitList = mutableListOf<Boolean>()
            for (move in moves) {
                val coordinates = move.toSquareWithoutHitOrMiss()
                val hitOrMiss: Boolean =
                    when (enemyBoard.board[coordinates.col.ordinal][coordinates.row.ordinal].type) {
                        Type.SEA -> false
                        Type.HIT -> throw ExceptionControllerAdvice.InvalidMove("You already shot here.")
                        Type.MISS -> throw ExceptionControllerAdvice.InvalidMove("You already shot here.")
                        else -> true
                    }
                if (hitOrMiss) {
                    coordinates.hitOrMiss = 'X'
                    enemyBoard.board[coordinates.col.ordinal][coordinates.row.ordinal].type = Type.HIT
                } else {
                    coordinates.hitOrMiss = 'O'
                    enemyBoard.board[coordinates.col.ordinal][coordinates.row.ordinal].type = Type.MISS
                }
                newMoves += "$move "
                hitList.add(hitOrMiss)
                if (isGameOver(enemyBoard.toString())) {
                    gamesRepository.endGame(gameId, playerId)
                    break
                }
            }
            gamesRepository.updatePlayerMoves(gameId, playerId, newMoves)
            gamesRepository.updatePlayerBoard(gameId, enemyPlayerId, enemyBoard.toString())
            hitList
        }
    }

    fun getPlayerId(gameId: String, username: String): Int {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.getPlayerId(gameId, username)
                ?: throw ExceptionControllerAdvice.PlayerNotFoundException("Respective player isn't in this game.")
        }
    }

    private fun isGameOver(board: String): Boolean {
        val fleetStatus = boardAnalysis(board)
        return fleetStatus.count { it.contains("Sunken") } == 5
    }


    /**
     * Validates if the introduced String contains positions with the same type of Piece (same ship Char)
     */
    private fun validateShipTypeAndDimension(ships: String): Char {
        val fleetSize = mutableMapOf('C' to 5, 'B' to 4, 'D' to 3, 'S' to 3, 'P' to 2)
        val shipParts = ships.split(" ")
        val shipChar = shipParts[0][2] //"c char, for carrier, for example"
        if (shipParts.size != fleetSize[shipChar.uppercaseChar()]) throw ExceptionControllerAdvice.InvalidShipPlacementException(
            "Invalid ship size"
        )
        for (part in shipParts) {
            if (part[2] != shipChar)
                throw ExceptionControllerAdvice.InvalidShipPlacementException("Invalid Ship Placement")
            if (!Row.values.contains(part[1].toRowOrNull())) throw ExceptionControllerAdvice.InvalidShipPlacementException(
                "Invalid Ship Placement"
            )
            if (!Column.values.contains(part[0].toColumnOrNull())) throw ExceptionControllerAdvice.InvalidShipPlacementException(
                "Invalid Ship Placement"
            )
        }
        return shipChar.uppercaseChar()
    }


    /**
     * Validates if the introduced ship positions are valid.
     */
    private fun validateShipPosition(ships: List<String>) {
        val firstSquare = ships[0].toSquare()
        val validPositions = buildPossibleFirstPiecePositions(firstSquare)
        val secondSquare = ships[1].toSquare()
        if (!validPositions.contains(secondSquare)) throw throw ExceptionControllerAdvice.InvalidShipPlacementException(
            "Invalid Ship Placement"
        )

        val mode = if (secondSquare.col != firstSquare.col) "Horizontal" else "Vertical"

        var direction = ""
        if (secondSquare.col.ordinal == firstSquare.col.ordinal - 1) direction = "Left"
        if (secondSquare.col.ordinal == firstSquare.col.ordinal + 1) direction = "Right"
        if (secondSquare.row.ordinal == firstSquare.row.ordinal + 1) direction = "Ascending"
        if (secondSquare.row.ordinal == firstSquare.row.ordinal - 1) direction = "Descending"

        for (i in 2..ships.size - 1) {
            val prevShip = ships[i - 1].toSquare()
            val currShip = ships[i].toSquare()
            if (mode == "Vertical") {
                if (direction == "Descending") {
                    if (currShip.row == firstSquare.row || currShip.row.ordinal != prevShip.row.ordinal - 1) throw ExceptionControllerAdvice.InvalidShipPlacementException(
                        "Invalid Ship Placement"
                    )
                } else {
                    if (currShip.row == firstSquare.row || currShip.row.ordinal != prevShip.row.ordinal + 1) throw ExceptionControllerAdvice.InvalidShipPlacementException(
                        "Invalid Ship Placement"
                    )
                }

            } else {
                if (direction == "Right") {
                    if (currShip.col == firstSquare.col || currShip.col.ordinal != prevShip.col.ordinal + 1) throw ExceptionControllerAdvice.InvalidShipPlacementException(
                        "Invalid Ship Placement"
                    )
                } else {
                    if (currShip.col == firstSquare.col || currShip.col.ordinal != prevShip.col.ordinal - 1) throw ExceptionControllerAdvice.InvalidShipPlacementException(
                        "Invalid Ship Placement"
                    )
                }
            }
        }
    }


    /**
     * First square, draws a cross (+), of possible positions around it
     * For the square C1 for example it returns: B1 D1 C2
     *
     *            B1 C1 D1
     *               C2
     *
     *
     */
    private fun buildPossibleFirstPiecePositions(square: Square): MutableList<Square> {
        val validPositions: MutableList<Square> = mutableListOf() //Para C1, vai ter por exemplo: A1 D1 C2
        val column = square.col
        val row = square.row
        //First square, draws a cross (+), of possible positions around it
        val possibleColumns = mutableListOf(
            (column.letter + 1).toColumnOrNull(),
            (column.letter - 1).toColumnOrNull()
        )
        val possibleRows = mutableListOf(
            (row.digit + 1).toRowOrNull(),
            (row.digit - 1).toRowOrNull()
        )

        possibleColumns.remove(null)
        possibleRows.remove(null)

        possibleColumns.forEach {
            validPositions.add(Square(it!!, row))
        }

        possibleRows.forEach {
            validPositions.add(Square(column, it!!))
        }
        return validPositions
    }


    /**
     *
     * Se a Request do cliente for uma string já com os ships todos no lugar no formato de Board.toString
     */
    fun postShips(gameId: String, username: String, ships: List<String>): String {
        val fleetCount = mutableMapOf('C' to 1, 'B' to 1, 'D' to 1, 'S' to 1, 'P' to 1)
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            val game = gamesRepository.getGame(gameId)
                ?: throw ExceptionControllerAdvice.GameNotFoundException("Introduced Game does not exist.")
            if (game.gameState != State.BUILDING) throw ExceptionControllerAdvice.WrongGameStateException("Game isn't in build phase.")
            val id = getPlayerId(gameId, username)
            val army = if (id == 1) Army.BLUE else Army.RED
            val board = Board(army)

            for (ship in ships) {
                //validação do tipo e tamanho do navio
                val shipType = validateShipTypeAndDimension(ship)

                fleetCount[shipType] = fleetCount[shipType]!! - 1

                //validação da posição
                val split = ship.split(" ")
                validateShipPosition(split)
            }

            fleetCount.forEach {
                if (fleetCount[it.key] != 0) throw ExceptionControllerAdvice.InvalidShipPlacementException("Wrong number of ships placed")
            }

            for (ship in ships) {
                val split = ship.split(" ")
                for (i in split.indices) {
                    val column = split[i][0].toColumnOrNull()
                    val row = split[i][1].toRowOrNull()
                    val position = board.board[column!!.ordinal][row!!.ordinal]
                    if (position.type == Type.SEA) {
                        //TODO Check around piece before put
                        board.board[column.ordinal][row.ordinal] = split[i][2].toPiece(army)
                    } else throw ExceptionControllerAdvice.InvalidShipPlacementException("Invalid Ship Placement") //caso esteja um navio já colocado nesse local
                }
            }
            val newBoard: String = board.toString()
            gamesRepository.updatePlayerBoard(gameId, id, newBoard)
            checkBuildingPhase(gameId, playerId = id)
            newBoard
        }
    }

    private fun checkBuildingPhase(gameId: String, playerId: Int) {
        transactionManager.run {
            val gamesRepository = it.gamesRepository
            val enemyId = if (playerId == 1) 2 else 1
            val enemyBoard = gamesRepository.getPlayerBoard(gameId, enemyId)
            var enemyReady = false
            enemyBoard.forEach {
                if (it != '_')
                    enemyReady = true
            }
            if (enemyReady) gamesRepository.startGame(gameId)
        }
    }

}
/**---------------------------------------------------------------------------**/


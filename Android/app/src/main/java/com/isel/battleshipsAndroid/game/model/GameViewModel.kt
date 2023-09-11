package com.isel.battleshipsAndroid.game.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isel.battleshipsAndroid.TAG
import com.isel.battleshipsAndroid.lobby.MatchInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val emptyBoard = "________________________________________________________________"

class GameViewModel (
    private val gameService: PlayService,
    private val info: MatchInfo
) : ViewModel() {

    private var _isReady by mutableStateOf(false)
    val isReady: Boolean
        get() = _isReady

    private var _isLoading by mutableStateOf(false)
    val isLoading: Boolean
        get() = _isLoading

    private val _currentGame = MutableStateFlow(GameLastState("WAITING", "", emptyBoard, emptyBoard, "", "", 0))
    val currentGame = _currentGame.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()


    private val _availableShips = MutableStateFlow(mutableMapOf(
        'c' to Ship(name = "carrier", length = 5),
        'b' to Ship(name = "battleship", length = 4),
        'd' to Ship(name = "destroyer", length = 3),
        's' to Ship(name = "submarine", length = 3),
        'p' to Ship(name = "patrolBoat", length = 2)
    ))
    val availableShips = _availableShips.asStateFlow()

    private val _currentlyPlacing = MutableStateFlow<Ship?>(null)
    val currentlyPlacing = _currentlyPlacing.asStateFlow()

    private val _placedShips = MutableStateFlow<ArrayList<Ship>>(arrayListOf())
    val placedShips = _placedShips.asStateFlow()

    private val _playerBoard = MutableStateFlow(Board())
    val playerBoard = _playerBoard.asStateFlow()

    private val _enemyBoard = MutableStateFlow(Board())
    val enemyBoard = _enemyBoard.asStateFlow()

    private val _hit = MutableStateFlow(false)
    val hit = _hit.asStateFlow()

    private val _processedInfo = MutableStateFlow(info)
    val processedInfo = _processedInfo.asStateFlow()

    fun getSecondPlayer(gameId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _processedInfo.value.player2 = gameService.getSecondPlayer(gameId)
            } catch (e: Exception) {
                Log.v(TAG, e.toString())
            }
        }
    }

    // placements in correct format for backend API
    private fun getShipsPlacements(): Array<String> {
        var placements: Array<String> = emptyArray()
        var positions = ""

        placedShips.value.forEach { ship ->
            getIndexes(ship).forEach{ index ->
                positions += "${indexToCoordinates(index)}${ship.name[0]} "
            }
            positions = positions.dropLast(1)
            placements = placements.plus(positions)
            positions = ""
        }

        return placements
    }

    fun selectShip(shipChar: Char) {
        _currentlyPlacing.value = _availableShips.value[shipChar]
    }

    fun placeShip() {
        _placedShips.value.add(currentlyPlacing.value!!)
        _playerBoard.value.board = buildingPlacements()
        _availableShips.value.remove(currentlyPlacing.value!!.name[0])
        _currentlyPlacing.value = null
    }

    fun resetBoard(){
        _placedShips.value = arrayListOf()
        _availableShips.value = mutableMapOf(
            'c' to Ship(name = "carrier", length = 5),
            'b' to Ship(name = "battleship", length = 4),
            'd' to Ship(name = "destroyer", length = 3),
            's' to Ship(name = "submarine", length = 3),
            'p' to Ship(name = "patrolBoat", length = 2)
        )
    }

    fun buildingPlacements(): Array<Piece> {
        val newBoard = Board()
        placedShips.value.forEach { ship ->
            getIndexes(ship).forEach { index ->
                newBoard.board[index] = ship.name[0].toPiece()
            }
        }
        return newBoard.board
    }

    fun gameStart() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                gameService.buildBoard(info.uuid, getShipsPlacements())
            } catch (e: Exception) {
                Log.v(TAG, e.toString())
                val errorMessage = e.toString().split(": ").last()
                _error.value = errorMessage
            }
        }
    }

    fun rotateShip() {
        if (currentlyPlacing.value != null) {
            currentlyPlacing.value!!.orientation = if (currentlyPlacing.value!!.orientation == "vertical") "horizontal" else "vertical"
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun refreshGame() {
        val result = viewModelScope.async(Dispatchers.IO) {
            try {
                 gameService.refreshedGameState(info.uuid)
            } catch (e: Exception) {
                Log.v(TAG, e.toString())
                val errorMessage = e.toString().split(": ").last()
                _error.value = errorMessage
                GameLastState("WAITING", "", emptyBoard, emptyBoard, "", "", 0)
            }
        }
        result.invokeOnCompletion {
            if (it == null) {
                _currentGame.value = result.getCompleted()

                if (currentGame.value.gameState == "ENDED" && (currentGame.value.board1 == "" || currentGame.value.board2 == "")) {
                    currentGame.value.board1 = emptyBoard
                    currentGame.value.board2 = emptyBoard
                }

                if (currentGame.value.gameState != "WAITING" && currentGame.value.gameState != "BUILDING") {
                    if (info.whoAmI == "PLAYER1") {
                        _playerBoard.value = Board(currentGame.value.board1)
                        _enemyBoard.value = Board(currentGame.value.board2)
                    } else {
                        _playerBoard.value = Board(currentGame.value.board2)
                        _enemyBoard.value = Board(currentGame.value.board1)
                    }
                }
            }
        }
    }

    fun makeMove(shots: Array<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            _hit.value =
                try {
                    gameService.makeMove(info.uuid, shots)
                } catch (e: Exception) {
                    Log.v(TAG, e.toString())
                    val errorMessage = e.toString().split(": ").last()
                    _error.value = errorMessage
                    false
                }
        }
    }

    fun handleForfeit() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                gameService.forfeitMatch(info.uuid)
            } catch (e: Exception) {
                Log.v(TAG, e.toString())
            }
        }
    }

    private fun overBoard(shipIndexes: ArrayList<Int>): Boolean {
        for (i in 0 until shipIndexes.size - 1) {
            if(((shipIndexes[i] % BOARD_ROWS) > (shipIndexes[i + 1] % BOARD_ROWS)) || (shipIndexes[i + 1] > 63))
                return true
        }
        return false
    }

    fun canPlaceCurrentShip(currentlyPlacing: Ship, position: String): Boolean {
        _currentlyPlacing.value!!.position = position

        val currentlyPlacingIndexes = getIndexes(currentlyPlacing)

        if (overBoard(currentlyPlacingIndexes))
            return false

        for (ship in placedShips.value) {
            for (index in getIndexes(ship)) {
                if(currentlyPlacingIndexes.contains(index))
                    return false
            }
        }
        return true
    }

    fun resetError() {
        _error.value = null
    }

}
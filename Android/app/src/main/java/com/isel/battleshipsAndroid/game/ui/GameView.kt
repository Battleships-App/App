package com.isel.battleshipsAndroid.game.ui

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PanToolAlt
import androidx.compose.material.icons.filled.Restore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isel.battleshipsAndroid.R
import com.isel.battleshipsAndroid.TAG
import com.isel.battleshipsAndroid.game.BOARD_SIDE
import com.isel.battleshipsAndroid.game.model.*
import com.isel.battleshipsAndroid.lobby.MatchInfo
import com.isel.battleshipsAndroid.lobby.ui.LobbyScreenTag
import com.isel.battleshipsAndroid.ui.SquareImageView
import com.isel.battleshipsAndroid.ui.TopBar
import com.isel.battleshipsAndroid.ui.theme.BattleshipsAndroidTheme


@Composable
fun GameView(
    onLeaveRequest: () -> Unit,  //Semelhante ao onBackRequest, mas efetua uma leaveMatch, pode-se usar onBackRequested alternativamente
    info: MatchInfo,
    currentGame: GameLastState,
    playerBoard: Board,
    enemyBoard: Board,
    availableShips: MutableMap<Char, Ship>,
    currentlyPlacing: Ship?,
    placedShips: ArrayList<Ship>,
    myTurn: () -> Boolean,
    selectShip: (Char) -> Unit,
    placeShip: () -> Unit,
    canPlaceCurrentShip: (ship: Ship, position: String) -> Boolean,
    placements: () -> Array<Piece>,
    makeMove: (Array<String>) -> Unit,
    gameStart: () -> Unit,
    rotateShip: () -> Unit,
    error: String?,
    onErrorReset: () -> Unit,
    resetBoard: () -> Unit
) {

    val view = rememberSaveable { mutableStateOf(false) }
    val readyButtonClicked = rememberSaveable { mutableStateOf(false) }

    var disabledButtonIndex: Int? = null
    val playerName = if (info.whoAmI == "PLAYER1") info.player1 else info.player2
    val enemyName = if (info.whoAmI == "PLAYER1") info.player2 else info.player1


    BattleshipsAndroidTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag(LobbyScreenTag),
            backgroundColor = MaterialTheme.colors.background,
            topBar = {
                if (currentGame.gameState != "ENDED") {
                    TopBar(
                        onLeaveRequested = { onLeaveRequest() },
                        onInspectRequested = {
                            view.value = !view.value
                        } //Para inspecionar a própria Board
                    )
                }
            },
        ) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                Log.v(TAG, currentGame.toString())
                if (error != null) {
                    Toast.makeText(LocalContext.current, error, Toast.LENGTH_SHORT).show()
                    onErrorReset()
                }
                when (currentGame.gameState) {
                    "WAITING" -> {
                        GameWaitingView()
                    }
                    "ENDED" -> {
                        GameEndedView(currentGame, info, onLeaveRequest)
                    }
                    "BUILDING" -> {
                        GameBuildingView(
                            playerName,
                            placements,
                            currentlyPlacing,
                            canPlaceCurrentShip,
                            currentGame,
                            myTurn,
                            placeShip,
                            makeMove,
                            placedShips,
                            readyButtonClicked,
                            gameStart,
                            resetBoard,
                            availableShips,
                            disabledButtonIndex,
                            selectShip,
                            rotateShip
                        )
                    }
                    "STARTED" -> {
                        GameStartedView(
                            view,
                            playerName,
                            playerBoard,
                            currentlyPlacing,
                            canPlaceCurrentShip,
                            currentGame,
                            myTurn,
                            placeShip,
                            makeMove,
                            enemyName,
                            enemyBoard
                        )
                    }
                }
            }
        }
    }
}



@Composable
private fun GameStartedView(
    view: MutableState<Boolean>,
    playerName: String,
    playerBoard: Board,
    currentlyPlacing: Ship?,
    canPlaceCurrentShip: (ship: Ship, position: String) -> Boolean,
    currentGame: GameLastState,
    myTurn: () -> Boolean,
    placeShip: () -> Unit,
    makeMove: (Array<String>) -> Unit,
    enemyName: String,
    enemyBoard: Board
) {
    Text(
        text = stringResource(id = R.string.game_game),
        style = MaterialTheme.typography.h5,
        color = MaterialTheme.colors.primaryVariant
    )
    if (view.value) {
        Text(
            text = stringResource(id = R.string.game_playerboard) + " $playerName",
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.error
        )
        BoardView(
            enemyBoard = false,
            board = playerBoard.board,
            currentlyPlacing = currentlyPlacing,
            canPlaceCurrentShip = canPlaceCurrentShip,
            gameState = currentGame.gameState,
            myTurn,
            placeShip,
            makeMove
        )
    } else {
        Text(
            text = stringResource(id = R.string.game_playerboard) + " $enemyName",
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.error
        )
        BoardView(
            enemyBoard = true,
            board = enemyBoard.board,
            currentlyPlacing = currentlyPlacing,
            canPlaceCurrentShip = canPlaceCurrentShip,
            gameState = currentGame.gameState,
            myTurn,
            placeShip,
            makeMove
        )
    }
    if (myTurn()) {
        Icon(
            Icons.Filled.PanToolAlt,
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Text(
            text = stringResource(id = R.string.game_myturn),
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.primaryVariant
        )
    } else {
        Icon(
            Icons.Default.HourglassBottom,
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Text(
            text = "...$enemyName " + stringResource(id = R.string.game_enemyturn),
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.primaryVariant
        )
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
private fun GameBuildingView(
    playerName: String,
    placements: () -> Array<Piece>,
    currentlyPlacing: Ship?,
    canPlaceCurrentShip: (ship: Ship, position: String) -> Boolean,
    currentGame: GameLastState,
    myTurn: () -> Boolean,
    placeShip: () -> Unit,
    makeMove: (Array<String>) -> Unit,
    placedShips: ArrayList<Ship>,
    readyButtonClicked: MutableState<Boolean>,
    gameStart: () -> Unit,
    resetBoard: () -> Unit,
    availableShips: MutableMap<Char, Ship>,
    disabledButtonIndex: Int?,
    selectShip: (Char) -> Unit,
    rotateShip: () -> Unit
) {
    val ships = availableShips.toList()
    val booleanShipPairs = ships.map { Pair(it.first,true) }
    val buttonStates = rememberSaveable { mutableStateOf(booleanShipPairs) } //List dos 5 navios a colocar, em par com um Booleano que indica se já foi selecionado

    Text(
        text = stringResource(id = R.string.game_buildinggame),
        style = MaterialTheme.typography.h5,
        color = MaterialTheme.colors.primaryVariant
    )
    Text(
        text = stringResource(id = R.string.game_playerboard) + " $playerName",
        style = MaterialTheme.typography.h5,
        color = MaterialTheme.colors.primary
    )
    BoardView(
        enemyBoard = false,
        board = placements(),
        currentlyPlacing = currentlyPlacing,
        canPlaceCurrentShip = canPlaceCurrentShip,
        gameState = currentGame.gameState,
        myTurn,
        placeShip,
        makeMove
    )
    if (placedShips.size == 5) {
        if (!readyButtonClicked.value) {
            Button(onClick = {
                gameStart()
                readyButtonClicked.value = true
            }) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = stringResource(id = R.string.game_ready),
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(id = R.string.game_ready))
            }
        } else {
            Text(
                text = stringResource(id = R.string.game_waitingfortheopponent),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Green
                )
            )
        }
    }
    if (!readyButtonClicked.value) {
        Text(
            text = stringResource(id = R.string.game_selectshipandtapboardtoplace),
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.primaryVariant
        )
        Button(onClick = {
            resetBoard()
            readyButtonClicked.value = false
            val newList = mutableListOf<Pair<Char,Boolean>>()
            for (char in buttonStates.value) {
                newList.add(Pair(char.first, true))
            }
            buttonStates.value = newList
        }) {
            Icon(
                Icons.Filled.Restore,
                contentDescription = stringResource(id = R.string.game_resetboard),
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.game_resetboard))
        }
        if (placedShips.size != 5) {
            Text(
                text = stringResource(id = R.string.game_shipslefttoplace),
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.primaryVariant
            )
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                val ships = availableShips.toList()
                items(ships.size) { index ->
                    val shipChar = ships[index].first
                    val wasPlaced = buttonStates.value.find { it.first==shipChar }!!.second
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant),
                        enabled = wasPlaced,
                        onClick = {
                        val newList = mutableListOf<Pair<Char,Boolean>>()
                        buttonStates.value.map {
                            if(it.first==shipChar) newList.add(Pair(shipChar,false)) else newList.add(Pair(buttonStates.value[index].first,true))
                        }
                        for (char in buttonStates.value) {
                            newList.add(Pair(char.first, true))
                        }
                        buttonStates.value = newList
                        selectShip(ships[index].second.name[0])
                    }) { if(wasPlaced) Text(text = ships[index].second.name) else Text(text = "Selected")}
                }
            }
            Button(onClick = { rotateShip() }) { Text(text = stringResource(id = R.string.game_rotateship)) }
        }
    }
}

@Composable
private fun GameWaitingView() {
    Text(
        text = stringResource(id = R.string.game_waitingforplayer),
        style = MaterialTheme.typography.h5,
        color = MaterialTheme.colors.primaryVariant
    )
}

@Composable
private fun GameEndedView(
    currentGame: GameLastState,
    info: MatchInfo,
    onLeaveRequest: () -> Unit
) {
    val winner = if (currentGame.result == 1) info.player1 else info.player2

    Text(
        text = "$winner " + stringResource(id = R.string.game_won),
        style = MaterialTheme.typography.h4,
        color = MaterialTheme.colors.primaryVariant
    )
    Button(onClick = { onLeaveRequest() }) { Text(text = stringResource(id = R.string.game_returntolobby)) }
}


@Composable
fun BoardView(
    enemyBoard: Boolean,
    board: Array<Piece>,
    currentlyPlacing: Ship?,
    canPlaceCurrentShip: (ship: Ship, position: String) -> Boolean,
    gameState: String,
    myTurn: () -> Boolean,
    placeShip: () -> Unit,
    makeMove: (Array<String>) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.Start) {
            for (i in 0..7) {
                Row() {
                    for (j in 0..7) {
                        SquareImageView(
                            background = board[i * BOARD_SIDE + j].type.toColor(enemyBoard),
                            onClick = {
                                if (currentlyPlacing != null && canPlaceCurrentShip(
                                        currentlyPlacing,
                                        indexToCoordinates(i * BOARD_SIDE + j)
                                    )
                                ) placeShip()
                                else if (enemyBoard && gameState == "STARTED" && myTurn()) makeMove(
                                    arrayOf(indexToCoordinates(i * BOARD_SIDE + j))
                                ) //if true hitIncrease
                            }
                        )
                    }
                }
            }
        }
    }
}


/**
 *
 * ------------------------------- PREVIEWS-----------------------------------
 *
 */
@Preview(showBackground = true)
@Composable
private fun GamePreviewBuilding() {
    val boardTest = "________________________________________________________________"
    val builtBoard = Board(boardTest)
    GameView(
        onLeaveRequest = {},  //Semelhante ao onBackRequest, mas efetua uma leaveMatch, pode-se usar onBackRequested alternativamente
        info = MatchInfo("", "test", "test", 1, ""),
        currentGame = GameLastState("BUILDING", "", "", "", "", "", 0),
        playerBoard = builtBoard,
        enemyBoard = builtBoard,
        availableShips = mutableMapOf('c' to Ship("carrier", length = 5)),
        currentlyPlacing = null,
        placedShips = arrayListOf(Ship("", length = 1)),
        myTurn = { true },
        selectShip = {},
        placeShip = {},
        canPlaceCurrentShip = { _, _ -> true },
        placements = { builtBoard.board },
        makeMove = {},
        gameStart = {},
        rotateShip = {},
        error = null,
        onErrorReset = {},
        resetBoard = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun GamePreviewPlayingMyTurn() {
    val boardTest = "__xxx___xxxx___oo____ooo_____o____xxx_____xx____oooooo_____o____"
    val builtBoard = Board(boardTest)
    GameView(
        onLeaveRequest = {},  //Semelhante ao onBackRequest, mas efetua uma leaveMatch, pode-se usar onBackRequested alternativamente
        info = MatchInfo("", "test", "test", 1, ""),
        currentGame = GameLastState("STARTED", "", "", "", "", "", 0),
        playerBoard = builtBoard,
        enemyBoard = builtBoard,
        availableShips = mutableMapOf(),
        currentlyPlacing = null,
        placedShips = arrayListOf(),
        myTurn = { true },
        selectShip = {},
        placeShip = {},
        canPlaceCurrentShip = { _, _ -> true },
        placements = { emptyArray() },
        makeMove = {},
        gameStart = {},
        rotateShip = {},
        error = null,
        onErrorReset = {},
        resetBoard = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun GamePreviewPlayingEnemyTurn() {
    val boardTest = "__xxx___xxxx___oo____ooo_____o____xxx_____xx____oooooo_____o____"
    val builtBoard = Board(boardTest)
    GameView(
        onLeaveRequest = {},  //Semelhante ao onBackRequest, mas efetua uma leaveMatch, pode-se usar onBackRequested alternativamente
        info = MatchInfo("", "test", "test", 1, ""),
        currentGame = GameLastState("", "", "", "", "", "", 0),
        playerBoard = builtBoard,
        enemyBoard = builtBoard,
        availableShips = mutableMapOf(),
        currentlyPlacing = null,
        placedShips = arrayListOf(),
        myTurn = { false },
        selectShip = {},
        placeShip = {},
        canPlaceCurrentShip = { _, _ -> true },
        placements = { emptyArray() },
        makeMove = {},
        gameStart = {},
        rotateShip = {},
        error = null,
        onErrorReset = {},
        resetBoard = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BoardPreview() {
    val boardTest = "__sss___xxxx___pp____ooo____ddd___xxx_____bbbb__oooooo___ccccc__"
    val builtBoard = Board(boardTest).board
    BoardView(
        enemyBoard = false,
        board = builtBoard,
        currentlyPlacing = null,
        canPlaceCurrentShip = { _, _ -> true },
        gameState = "",
        myTurn = { true },
        placeShip = {},
        makeMove = {}
    )
}

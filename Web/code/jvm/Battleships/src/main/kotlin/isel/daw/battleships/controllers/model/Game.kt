package isel.daw.battleships.controllers.model

data class GameIdModel(val gameId: String)

data class GameOpenModel(val uuid: String, val player1: String, val spr: Int)

data class GameStatus(val status: String)

data class GameBoard(val board: String)

data class GameLastState(val gameState: String,val turn :String ,val board1: String, val board2: String, val moves1: String,val moves2:String,val result: Int)

data class HitOrMiss(val hitOrMiss : List<Boolean>)

data class FleetStatus( val fleetStatus : List<String>)
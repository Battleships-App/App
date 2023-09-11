package isel.daw.battleships.model


data class Player(
    val username: String, val uuid: String, val hashPassword: String, var games: Int = 0, var points: Int = 0
)
data class PlayerDTO(val username: String,val games: Int,val points: Int)

package isel.daw.battleships.controllers

import isel.daw.battleships.BattleshipsApplication
import isel.daw.battleships.controllers.model.GameOpenModel
import isel.daw.battleships.controllers.model.User
import isel.daw.battleships.database.jdbi.JdbiTransactionManager
import isel.daw.battleships.http.Rels
import isel.daw.battleships.http.Uris
import isel.daw.battleships.http.infra.SirenModel
import isel.daw.battleships.http.infra.siren
import isel.daw.battleships.model.GameDTO
import isel.daw.battleships.model.PlayerDTO
import isel.daw.battleships.services.ServicesGame
import isel.daw.battleships.services.ServicesPlayers
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/me")  //  {user} - > /game ; Alteração neste novo update, pois variaveis de Path não são retiradas aqui mas sim na anotação da função
class ControllerMe(
    private val services_game: ServicesGame = ServicesGame(JdbiTransactionManager(jdbi = BattleshipsApplication().jdbi()) )
){

    @GetMapping("/")
    fun getMePage(user: User): ResponseEntity<*> {
        return ResponseEntity
            .status(200)
            .header("Content-Type", SirenModel.MEDIA_TYPE)
            .body(siren("${user.name}'s Page") {
                clazz("me")
                link(Uris.Home.me(), Rels.SELF)
                link(Uris.Home.home(), Rels.Home.HOME)
                action("getMyInformation",Uris.Me.me(user.name),HttpMethod.GET,MediaType.APPLICATION_JSON.toString()){}
                action("getRunningGames", Uris.Me.running(), HttpMethod.GET, MediaType.APPLICATION_JSON.toString()) {}
                action("getGameHistory", Uris.Me.history(), HttpMethod.GET, MediaType.APPLICATION_JSON.toString()) {}
                action("getOpenGames", Uris.Me.open(), HttpMethod.GET, MediaType.APPLICATION_JSON.toString()) {}
            })
    }

    @GetMapping("/history")
    fun getLastGamesByPlayer(user: User): ResponseEntity<*> {
        val gameList =
            services_game.getLastGamesByPlayer(user.name)
        val gameListDTO = mutableListOf<GameDTO>()
        gameList.forEach {
            gameListDTO.add(
                GameDTO(
                    it.uuid,
                    it.gameState.toString(),
                    it.nextPlay.toString(),
                    it.player1Username,
                    it.player2Username,
                    (if (user.name == it.player1Username) it.board1 else it.board2),
                    it.moves1,
                    it.moves2,
                    it.shotsPerRound,
                    it.result
                )
            )
        }
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(gameListDTO)
    }

    @GetMapping("/running")
    fun getRunningGamesByPlayer(user: User): ResponseEntity<*> {
        val gameList = services_game.getRunningGamesByPlayer(user.name)
        val gameListDTO = mutableListOf<GameDTO>()
        gameList.forEach {
            val playerid = services_game.getPlayerId(it.uuid, user.name)
            val board = if (playerid == 1) it.board1 else it.board2
            gameListDTO.add(
                GameDTO(
                    it.uuid,
                    it.gameState.toString(),
                    it.nextPlay.toString(),
                    it.player1Username,
                    it.player2Username,
                    board,
                    it.moves1,
                    it.moves2,
                    it.shotsPerRound,
                    0
                )
            )
        }
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(gameListDTO)
    }


    @GetMapping("/open")
    fun getOpenGamesByPlayer(user: User): ResponseEntity<*> {
        val gameList = services_game.getOpenGamesByPlayer(user.name)
        val gameListDTO = mutableListOf<GameOpenModel>()
        gameList.forEach {
            gameListDTO.add(
                GameOpenModel(
                    it.uuid,
                    it.player1Username,
                    it.shotsPerRound,
                )
            )
        }
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(gameListDTO)
    }
}
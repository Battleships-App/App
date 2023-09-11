package isel.daw.battleships.controllers

import isel.daw.battleships.BattleshipsApplication
import isel.daw.battleships.controllers.model.*
import isel.daw.battleships.database.jdbi.JdbiTransactionManager
import isel.daw.battleships.http.Rels
import isel.daw.battleships.http.Uris
import isel.daw.battleships.http.infra.FieldModel
import isel.daw.battleships.http.infra.SirenModel
import isel.daw.battleships.http.infra.siren
import isel.daw.battleships.model.GameDTO
import isel.daw.battleships.services.ServicesGame
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


//This controller should only be available to authenticated users
@RestController
@RequestMapping("/game")  //  {user} - > /game ; Alteração neste novo update, pois variaveis de Path não são retiradas aqui mas sim na anotação da função
class ControllerGame(
    private val services_game: ServicesGame = ServicesGame(JdbiTransactionManager(jdbi = BattleshipsApplication().jdbi()))
) {

    @GetMapping("/")
    fun getRoomsPage(user: User): ResponseEntity<*> {
        val gameList = services_game.getAllOpenGames()
        val gameListDTO = mutableListOf<GameOpenModel>()
        gameList.forEach {
            gameListDTO.add(
                GameOpenModel(
                    it.uuid,
                    it.player1Username,
                    it.shotsPerRound
                )
            )
        }
        return ResponseEntity
            .status(200)
            .header("Content-Type", SirenModel.MEDIA_TYPE)
            .body(siren(gameListDTO) {
                clazz("rooms")
                link(Uris.Home.rooms(), Rels.SELF)
                link(Uris.Home.home(), Rels.Home.HOME)
                action(
                    "createGame",
                    Uris.Rooms.createGame(3),
                    HttpMethod.POST,
                    MediaType.APPLICATION_JSON.toString()
                ) {}
                action("joinGame", Uris.Rooms.joinGame("gameId"), HttpMethod.PUT, SirenModel.MEDIA_TYPE) {}
            })
    }


    @PutMapping("/join")
    fun joinGame(@RequestParam gameId: String, user: User): ResponseEntity<*> {
        val game = services_game.joinGame(gameId, user.name)
        val board = if (user.name == game.player1Username) game.board1 else game.board2
        return ResponseEntity
            .status(200)
            .header("Content-Type", SirenModel.MEDIA_TYPE)
            .body(siren(
                GameDTO(
                    game.uuid,
                    game.gameState.toString(),
                    game.nextPlay.toString(),
                    game.player1Username,
                    game.player2Username,
                    board,
                    game.moves1,
                    game.moves2,
                    game.shotsPerRound,
                    game.result
                )
            ) {
                clazz("Game $gameId")
                link(Uris.Rooms.joinGame(gameId), Rels.SELF)
                link(Uris.Rooms.allOpen(), Rels.Rooms.ALLOPEN)
                action(
                    "build",
                    Uris.Rooms.Game.build(gameId),
                    HttpMethod.PUT,
                    MediaType.APPLICATION_JSON.toString()
                ) {
                    textField("shipsList")
                }
                action(
                    "refresh",
                    Uris.Rooms.Game.refresh(gameId),
                    HttpMethod.GET,
                    MediaType.APPLICATION_JSON.toString()
                ) {}
                action(
                    "play",
                    Uris.Rooms.Game.play(gameId),
                    HttpMethod.PUT,
                    MediaType.APPLICATION_JSON.toString()
                ) {
                    textField("movesList")
                }
                action(
                    "status",
                    Uris.Rooms.Game.status(gameId),
                    HttpMethod.GET,
                    MediaType.APPLICATION_JSON.toString()
                ) {}
                action(
                    "myFleetStatus",
                    Uris.Rooms.Game.myFleetStatus(gameId),
                    HttpMethod.GET,
                    MediaType.APPLICATION_JSON.toString()
                ) {}
                action(
                    "myEnemyFleetStatus",
                    Uris.Rooms.Game.myEnemyFleetStatus(gameId),
                    HttpMethod.GET,
                    MediaType.APPLICATION_JSON.toString()
                ) {}
            })
    }


    @PostMapping("/create")
    fun postGame(@RequestParam shotsPerRound: Int, user: User): ResponseEntity<*> {
        val uuid = services_game.postGame(user.name, shotsPerRound)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body(GameIdModel(uuid))
    }

    @PutMapping("/forfeit")
    fun forfeitGame(@RequestParam gameId: String, user: User): ResponseEntity<*> {
        val uuid = services_game.forfeitGame(gameId, user.name)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(GameIdModel(uuid))
    }

    /*
    @PutMapping("/exit")
    fun exitGame(@RequestParam gameId: String, user: User): ResponseEntity<*> {
        val uuid = services_game.exitGame(user.name)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(GameIdModel(uuid))
    }

     */

    @PutMapping("/build")
    fun buildBoard(@RequestParam gameId: String, @RequestBody ships: List<String>, user: User): ResponseEntity<*> {
        val board = services_game.postShips(gameId, user.name, ships)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body(GameBoard(board))
    }

    @GetMapping("/refresh")
    fun refresh(@RequestParam gameId: String, user: User): ResponseEntity<*> {
        val game = services_game.getLastStateOfGame(gameId, user.name)
        //val board = if (user.name == game.player1Username) game.board1 else game.board2
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(GameLastState(game.gameState.toString(),game.nextPlay.toString(), game.board1, game.board2, game.moves1, game.moves2, game.result))
    }


    @PutMapping("/play")
    fun makeMove(@RequestParam gameId: String, @RequestBody moves: List<String>, user: User): ResponseEntity<*> {
        val hitOrMiss = services_game.hitOrMiss(gameId, user.name, moves)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body(HitOrMiss(hitOrMiss))
    }

    @GetMapping("/status")                   //gameId por query String, não no path
    fun getGameStatus(@RequestParam gameId: String, user: User): ResponseEntity<*> {
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(GameStatus(services_game.getGameStatus(gameId)))
    }

    @GetMapping("/status/fleet")
    fun getMyFleetStatus(@RequestParam gameId: String, user: User): ResponseEntity<*> {
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(FleetStatus(services_game.getMyFleetStatus(gameId, user.name)))
    }

    @GetMapping("/status/enemyfleet")
    fun getEnemyFleetStatus(@RequestParam gameId: String, user: User): Any {
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(FleetStatus(services_game.getEnemyFleetStatus(gameId, user.name)))
    }
}




package isel.daw.battleships.controllers


import isel.daw.battleships.BattleshipsApplication
import isel.daw.battleships.controllers.model.SignInModel
import isel.daw.battleships.controllers.model.Token
import isel.daw.battleships.database.jdbi.JdbiTransactionManager
import isel.daw.battleships.http.Rels
import isel.daw.battleships.http.Uris
import isel.daw.battleships.http.infra.SirenModel
import isel.daw.battleships.http.infra.siren
import isel.daw.battleships.services.ServicesPlayers
import isel.daw.battleships.model.*
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/players")
class ControllerPlayers(private val services_players: ServicesPlayers = ServicesPlayers(JdbiTransactionManager(jdbi = BattleshipsApplication().jdbi()))) {


    @GetMapping("/")
    fun getLeaderBoardPage(): ResponseEntity<*> {
        return ResponseEntity
            .status(200)
            .header("Content-Type", SirenModel.MEDIA_TYPE)
            .body(siren("You're in the LeaderBoard Page") {
                clazz("leaderboard")
                link(Uris.Home.leaderboard(), Rels.SELF)
                link(Uris.Home.home(), Rels.Home.HOME)
                action(
                    "getPlayerByUsername",
                    Uris.LeaderBoard.playerById("name"),
                    HttpMethod.GET,
                    MediaType.APPLICATION_JSON.toString()
                ) {}
                action(
                    "getRankings",
                    Uris.LeaderBoard.rankings(4),
                    HttpMethod.GET,
                    MediaType.APPLICATION_JSON.toString()
                ) {}
            })
    }

    @GetMapping("/player")
    fun getPlayerByUsername(@RequestParam username: String): ResponseEntity<*> {
        val player = services_players.getPlayerByUsername(username)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(PlayerDTO(player.username, player.games, player.points))
    }

    @GetMapping("/rankings")
    fun getRankings(@RequestParam pages: Int): ResponseEntity<*> {
        val playerList = services_players.getRankings(pages)
        if (playerList.isEmpty()) return ResponseEntity.status(200).body(emptyList<PlayerDTO>())
        val playerListDTO = mutableListOf<PlayerDTO>()
        playerList.forEach { playerListDTO.add(PlayerDTO(it.username, it.games, it.points)) }
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(playerListDTO)
    }


    @PostMapping("/signup")
    fun signUp(@RequestBody signIn: SignInModel): ResponseEntity<*> {
        val newPlayer: String = services_players.createPlayer(signIn.username, signIn.password)
        return ResponseEntity
            .status(201)
            .header("Set-Cookie", "token=${newPlayer};Path=/;HttpOnly")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Token(newPlayer))
    }


    @PostMapping("/signin")
    fun signIn(@RequestBody signIn: SignInModel): ResponseEntity<*> {
        val uuid = services_players.authPlayer(signIn.username, signIn.password)
        return ResponseEntity
            .status(201)
            .header("Set-Cookie", "token=${uuid};Path=/;HttpOnly")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Token(uuid))
    }


    @PostMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<*> {
        val cookie = request.getHeader("Cookie")
        println(cookie)
        if (cookie != null) {
            response.addHeader("Set-Cookie", "$cookie;Path=/;HttpOnly;Max-Age=0")
        }
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Token(cookie))
    }
}

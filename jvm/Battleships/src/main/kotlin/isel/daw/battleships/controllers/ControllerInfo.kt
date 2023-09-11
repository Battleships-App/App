package isel.daw.battleships.controllers

import isel.daw.battleships.BattleshipsApplication
import isel.daw.battleships.controllers.model.SignInModel
import isel.daw.battleships.database.jdbi.JdbiTransactionManager
import isel.daw.battleships.http.Rels
import isel.daw.battleships.http.Uris
import isel.daw.battleships.http.infra.FieldModel
import isel.daw.battleships.http.infra.SirenModel
import isel.daw.battleships.http.infra.siren
import isel.daw.battleships.services.ServicesInfo
import isel.daw.battleships.model.SystemInfo
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

//This controller is available to anyone
@RestController
@RequestMapping("/")
class ControllerInfo(private val services_info: ServicesInfo = ServicesInfo(JdbiTransactionManager(jdbi = BattleshipsApplication().jdbi()))) {

    /**
     * Obtain information about the system, such as the system authors and the system version, by an unauthenticated user.
     */
    @GetMapping("/info")
    fun getSystemInfo(): ResponseEntity<*> {
        return ResponseEntity
            .status(200)
            .header("Content-Type", SirenModel.MEDIA_TYPE)
            .body(siren(services_info.getSystemInfo()) {
                clazz("info")
                link(Uris.Home.info(), Rels.SELF)
                link(Uris.Home.home(), Rels.Home.HOME)
            })
    }

    @GetMapping("/")
    fun getHomePage(): ResponseEntity<*> {
        return ResponseEntity
            .status(200)
            .header("Content-Type", SirenModel.MEDIA_TYPE)
            .body(siren("Welcome To Battleships API") {
                clazz("home")
                link(Uris.Home.home(), Rels.SELF)
                link(Uris.Home.me(),Rels.Home.ME)
                link(Uris.Home.rooms(), Rels.Home.ROOMS)
                link(Uris.Home.leaderboard(), Rels.Home.LEADERBOARD)
                link(Uris.Home.info(), Rels.Home.INFO)
                action("signin", Uris.Home.signIn(), HttpMethod.POST, MediaType.APPLICATION_JSON.toString()) {
                    textField("username")
                    textField("password")
                }
                action("signup", Uris.Home.signUp(), HttpMethod.POST, MediaType.APPLICATION_JSON.toString()) {
                    textField("username")
                    textField("password")
                }
            })
    }

}

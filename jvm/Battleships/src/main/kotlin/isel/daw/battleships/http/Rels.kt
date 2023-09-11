package isel.daw.battleships.http

import isel.daw.battleships.http.infra.LinkRelation
import org.springframework.web.util.UriTemplate
import java.net.URI

object Rels {
    val SELF = LinkRelation("self")

    object Home {
        val HOME = LinkRelation("home")
        val ME = LinkRelation("me")
        val ROOMS = LinkRelation("rooms")
        val LEADERBOARD = LinkRelation("leaderboard")
        val INFO = LinkRelation("info")
        val SIGNIN = LinkRelation("signin")
        val SIGNUP = LinkRelation("signup")
    }

    object Me {
        val ME = LinkRelation("me")
        val RUNNING = LinkRelation("running")
        val OPEN = LinkRelation("open")
        val HISTORY = LinkRelation("history")
    }

    object Rooms {
        val ALLOPEN = LinkRelation("rooms")
        val CREATEGAME = LinkRelation("creategame")
        val JOINGAME = LinkRelation("joingame")

        object Game {
            val INSIDEGAME = LinkRelation("game")
            val BUILD = LinkRelation("build")
            val REFRESH = LinkRelation("refresh")
            val PLAY = LinkRelation("play")
            val STATUS = LinkRelation("status")
            val MYFLEETSTATUS = LinkRelation("fleet")
            val MYENEMYFLEETSTATUS = LinkRelation("enemyfleet")
        }
    }

    object Info {
    }

    object LeaderBoard {
        val PLAYER_BY_ID = LinkRelation("player")
        val RANKINGS = LinkRelation("rankings")

    }


}
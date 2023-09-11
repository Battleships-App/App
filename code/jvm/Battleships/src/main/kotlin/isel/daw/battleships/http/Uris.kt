package isel.daw.battleships.http

import isel.daw.battleships.controllers.model.SignInModel
import isel.daw.battleships.model.Game
import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {

    object Home {
        private const val HOME = "/"
        private const val ME = "/me"
        private const val ROOMS = "/game/"
        private const val LEADERBOARD = "/players"
        private const val INFO = "/info"
        private const val SIGNIN = "/players/signin"
        private const val SIGNUP = "/players/signup"

        fun home(): URI = URI(HOME)
        fun me(): URI = URI(ME)
        fun rooms(): URI = URI(ROOMS)
        fun info(): URI = URI(INFO)
        fun leaderboard(): URI = URI(LEADERBOARD)
        fun signIn(): URI = URI(SIGNIN)
        fun signUp(): URI = URI(SIGNUP)
    }

    object Me {
        private const val ME = "/players/player?username={me}"
        private const val RUNNING = "/me/running"
        private const val OPEN = "/me/open"
        private const val HISTORY = "/me/history"

        fun me(name: String) = UriTemplate(ME).expand(name)
        fun running(): URI = URI(RUNNING)
        fun open(): URI = URI(OPEN)
        fun history(): URI = URI(HISTORY)

    }

    object Rooms {
        private const val ALLOPEN = "/game/"
        private const val CREATEGAME = "/game/create?shotsPerRound={shotsPerRound}"
        private const val JOINGAME = "/game/join?gameId={gameId}"
        fun allOpen(): URI = URI(ALLOPEN)
        fun createGame(shotsPerRound: Int): URI = UriTemplate(CREATEGAME).expand(shotsPerRound)
        fun joinGame(gameId: String): URI = UriTemplate(JOINGAME).expand(gameId)

        object Game {
            private const val INSIDEGAME = "/game/join?gameId={gameId}"
            private const val BUILD = "/game/build?gameId={gameId}"
            private const val REFRESH = "/game/refresh?gameId={gameId}"
            private const val PLAY = "/game/play?gameId={gameId}"
            private const val STATUS = "/game/status?gameId={gameId}"
            private const val MYFLEETSTATUS = "/game/status/fleet?gameId={gameId}"
            private const val MYENEMYFLEETSTATUS = "/game/status/enemyfleet?gameId={gameId}"
            fun insideGame(gameId : String) = UriTemplate(INSIDEGAME).expand(gameId)
            fun build(gameId : String) = UriTemplate(BUILD).expand(gameId)
            fun refresh(gameId : String) = UriTemplate(REFRESH).expand(gameId)
            fun play(gameId : String) = UriTemplate(PLAY).expand(gameId)
            fun status(gameId : String) = UriTemplate(STATUS).expand(gameId)
            fun myFleetStatus(gameId : String) = UriTemplate(MYFLEETSTATUS).expand(gameId)
            fun myEnemyFleetStatus(gameId : String) = UriTemplate(MYENEMYFLEETSTATUS).expand(gameId)

        }

    }

    object Info {

    }

    object LeaderBoard {
        const val PLAYER_BY_ID = "/players/player?username={name}"
        const val RANKINGS = "/players/rankings?pages={usersNumber}"

        fun playerById(name: String) = UriTemplate(PLAYER_BY_ID).expand(name)
        fun rankings(usersNumber: Int): URI = UriTemplate(RANKINGS).expand(usersNumber)

    }


}
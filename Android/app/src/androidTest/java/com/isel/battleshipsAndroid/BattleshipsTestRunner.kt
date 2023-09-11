package com.isel.battleshipsAndroid

import com.isel.battleshipsAndroid.about.model.AboutService
import com.isel.battleshipsAndroid.about.model.Author
import com.isel.battleshipsAndroid.about.model.Info
import com.isel.battleshipsAndroid.leaderboard.model.LeaderboardService
import com.isel.battleshipsAndroid.leaderboard.model.PlayerInfo
import com.isel.battleshipsAndroid.login.model.LoginService
import com.isel.battleshipsAndroid.login.model.Token
import com.isel.battleshipsAndroid.login.model.UserInfo
import com.isel.battleshipsAndroid.login.model.UserInfoRepository
import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay


val localTestPlayer = PlayerInfo("local",0,0)

val otherTestPlayersInLobby: List<PlayerInfo> = buildList {
    repeat(3) {
        add(PlayerInfo("remote $it",it,0 ))
    }
}

/**
 * The service locator to be used in the instrumented tests.
 */
class BattleshipsTestApplication() : DependenciesContainer, Application() {

    /**
     * This mock object will return a userInfo with username "local" and bearer "test-token"
     */
    override var userInfoRepo: UserInfoRepository =
        mockk(relaxed = true) {
            every { userInfo } returns UserInfo(localTestPlayer.username, "test-token")
        }

    /**
     * This mock object will return a list of 100 players when the rankings function is called with any input.
     * The players will be named "player 1-1", "player 1-2", ..., "player 10-10" and will have games and points
     * equal to their index multiplied by their page number.
     * For example, calling leaderboardService.rankings(3) will return a list of 30 players named
     * "player 1-1", "player 1-2", ..., "player 3-10"with games equal to their index multiplied
     * by their page number and points equal to their page number.
     */
    override var leaderboardService: LeaderboardService =
        mockk(relaxed = true) {
            coEvery { rankings(any()) } returns (1..10).flatMap { page ->
                (1..10).map { index ->
                    PlayerInfo("player $page-$index", page * index, page)
                }
            }

            coEvery { getPlayerInfo(any()) } answers {
                val name = firstArg<String>()
                val page = name.split("-").first().toInt()
                val index = name.split("-").last().toInt()
                PlayerInfo(name, page * index, page)
            }
        }

    /**
     * This mock returns test-token for any login or register call
     */
    override var loginService: LoginService =
        mockk {
            // Define the behavior of the login function to return a value after a delay
            coEvery { login(any(), any()) } coAnswers {
                delay(1000)
                Token("test-token")
            }

            // Define the behavior of the register function to return a value after a delay
            coEvery { register(any(), any()) } coAnswers {
                delay(1000)
                Token("test-token")
            }
        }

    /**
     * This mock returns a list of  1 Author with fields "testeName" for Name, "testeEmail" for email and "testeGithub" for github.
     */
    override var aboutService: AboutService =
        mockk(relaxed = true) {
            coEvery { getInfo() } coAnswers {
                Info("0.1", listOf(Author("testeName","testeEmail","testeGithub")))
            }
        }

}

@Suppress("unused")
class BattleshipsTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, BattleshipsTestApplication::class.java.name, context)
    }
}

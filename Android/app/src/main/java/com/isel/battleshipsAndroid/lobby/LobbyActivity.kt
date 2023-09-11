package com.isel.battleshipsAndroid.lobby

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.isel.battleshipsAndroid.DependenciesContainer
import com.isel.battleshipsAndroid.game.GameActivity
import com.isel.battleshipsAndroid.lobby.model.LobbyViewModel
import com.isel.battleshipsAndroid.lobby.model.RealLobbyService
import com.isel.battleshipsAndroid.lobby.ui.LobbyState
import com.isel.battleshipsAndroid.lobby.ui.LobbyView
import com.isel.battleshipsAndroid.utils.viewModelInit
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import okhttp3.OkHttpClient

class LobbyActivity : ComponentActivity() {

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            //.cache(Cache(directory = cacheDir, maxSize = 50 * 1024 * 1024))
            .build()
    }

    private val jsonEncoder: Gson by lazy {
        GsonBuilder()
            .create()
    }

    private val repo by lazy {
        (application as DependenciesContainer)
    }

    private val viewModel by viewModels<LobbyViewModel> {
        viewModelInit {
            require(repo.userInfoRepo.userInfo != null)
            LobbyViewModel(RealLobbyService(repo.userInfoRepo.userInfo!!, httpClient, jsonEncoder))
        }
    }

    companion object {
        fun navigate(origin: Activity) {
            with(origin) {
                val intent = Intent(this, LobbyActivity::class.java)
                startActivity(intent)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val error by viewModel.error.collectAsState()
            val games by viewModel.games.collectAsState()
            LobbyView(
                state = LobbyState(games,error),
                onRefreshRequest = { viewModel.fetchRooms() },
                onJoinGame = { gameId ->
                    val game = games.find { it.uuid == gameId }
                    if (game != null) {
                        viewModel.joinGame(gameId)
                        val info = MatchInfo(
                            gameId,
                            game.player1,
                            repo.userInfoRepo.userInfo!!.username,
                            whoAmI = "PLAYER2"
                        )
                        GameActivity.navigate(this, info)
                    }
                },
                onCreateGame = { spr ->
                    viewModel.createGame(spr) { uuid ->
                        val info = MatchInfo(
                            uuid,
                            repo.userInfoRepo.userInfo!!.username,
                            "waiting...",
                            spr,
                            whoAmI = "PLAYER1"
                        )
                        GameActivity.navigate(this, info)
                    }
                },
                onBackRequest = { finish() },
                onErrorReset = { viewModel.resetError() },
            )
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchRooms()
            }
        }
    }
}

@Parcelize
data class MatchInfo (
    val uuid: String,
    val player1: String,
    var player2: String,
    var spr: Int = 1,
    val whoAmI: String
    ) : Parcelable




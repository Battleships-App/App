package com.isel.battleshipsAndroid.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.isel.battleshipsAndroid.DependenciesContainer
import com.isel.battleshipsAndroid.about.AboutActivity
import com.isel.battleshipsAndroid.lobby.LobbyActivity
import com.isel.battleshipsAndroid.leaderboard.LeaderboardActivity
import com.isel.battleshipsAndroid.login.LoginActivity
import com.isel.battleshipsAndroid.me.MeActivity
import com.isel.battleshipsAndroid.utils.viewModelInit

class HomeActivity() : ComponentActivity() {

    private val repo by lazy {
        (application as DependenciesContainer)
            .userInfoRepo
    }

    private val viewModel: HomeViewModel by viewModels {
        viewModelInit {
            HomeViewModel(repo)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val loggedState by viewModel.isLoggedIn.collectAsState()
            HomeView(
                state = HomeScreenState(null, loggedState),
                onLogoutRequest = {
                    if (loggedState) {
                        repo.userInfo = null
                        viewModel.logout()
                        LoginActivity.navigate(this)
                    }
                },
                onMeRequest = {
                    if (loggedState)
                        MeActivity.navigate(this)
                    else
                        LoginActivity.navigate(this)
                },
                onFindGameRequest = {
                    if (loggedState)
                        LobbyActivity.navigate(this)
                    else
                        LoginActivity.navigate(this)
                },
                onLeaderboardRequest = { LeaderboardActivity.navigate(this) },
                onInfoRequest = { AboutActivity.navigate(this) },
                onSignInOrSignUpRequest = {
                    if (!loggedState)
                        LoginActivity.navigate(this)
                },
                onExitRequest = { finishAndRemoveTask() }
            )

            lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    if (repo.userInfo != null) {
                        viewModel.login()
                    }
                }
            })
        }
    }
}


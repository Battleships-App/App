package com.isel.battleshipsAndroid.leaderboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.isel.battleshipsAndroid.DependenciesContainer
import com.isel.battleshipsAndroid.leaderboard.model.LeaderboardViewModel
import com.isel.battleshipsAndroid.leaderboard.ui.LeaderboardState
import com.isel.battleshipsAndroid.leaderboard.ui.LeaderboardView
import com.isel.battleshipsAndroid.ui.LoadingState
import com.isel.battleshipsAndroid.ui.RefreshingState
import com.isel.battleshipsAndroid.utils.viewModelInit


class LeaderboardActivity : ComponentActivity() {

    private val viewModel by viewModels<LeaderboardViewModel> {
        viewModelInit {
            val app = (application as DependenciesContainer)
            LeaderboardViewModel(app.leaderboardService)
        }
    }

    companion object {
        fun navigate(origin: Activity) {
            with(origin) {
                val intent = Intent(this, LeaderboardActivity::class.java)
                startActivity(intent)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchRankings(10)
        setContent {
            val error by viewModel.error.collectAsState()
            val playerFound by viewModel.playerFound.collectAsState()
            val rankings by viewModel.rankings.collectAsState()
            LeaderboardView(
                state = LeaderboardState(
                    playerFound,
                    rankings,
                    error,
                ),
                onFindPlayer = {username -> viewModel.fetchPlayerInfo(username) },
                onBackRequest = { finish() },
                onErrorReset = {viewModel.resetError()},
            )
        }
    }
}

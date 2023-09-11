package com.isel.battleshipsAndroid.leaderboard.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isel.battleshipsAndroid.TAG
import com.isel.battleshipsAndroid.login.model.Token
import com.isel.battleshipsAndroid.login.model.UserInfoRepository
import com.isel.battleshipsAndroid.utils.PlayerDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * View model for the Leaderboard Screen hosted by [LeaderboardActivity]
 */
class LeaderboardViewModel(
    val leaderboard: LeaderboardService,
) : ViewModel() {

    private val _rankings = MutableStateFlow<List<PlayerInfo>>(emptyList())
    val rankings = _rankings.asStateFlow()

    private val _playerFound = MutableStateFlow<PlayerInfo?>(null)
    val playerFound = _playerFound.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()


    /** ----------------------------------------------------------------------- */

    fun fetchRankings(pages : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _rankings.value=
                try {
                    leaderboard.rankings(pages)
                } catch (e: Exception) {
                    Log.v(TAG, e.toString())
                    val errorMessage = e.toString().split(": ").last()
                    _error.value = errorMessage
                    emptyList()
                }
        }
    }

    fun fetchPlayerInfo(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _playerFound.value =
                try {
                    leaderboard.getPlayerInfo(name)
                } catch (e: Exception) {
                    Log.v(TAG, e.toString())
                    val errorMessage = e.toString().split(": ").last()
                    _error.value = errorMessage
                    null
                }
        }
    }

    fun resetError() {
        _error.value = null
    }

}
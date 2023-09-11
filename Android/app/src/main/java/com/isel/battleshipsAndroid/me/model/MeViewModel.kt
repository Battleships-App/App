package com.isel.battleshipsAndroid.me.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isel.battleshipsAndroid.TAG
import com.isel.battleshipsAndroid.game.model.*
import com.isel.battleshipsAndroid.leaderboard.model.PlayerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * View model for the Me Screen hosted by [MeActivity]
 */
class MeViewModel(
    private val me: MeService,
) : ViewModel() {

    private val _myInfo = MutableStateFlow<PlayerInfo?>(null)
    val myInfo = _myInfo.asStateFlow()

    private val _myGamesHistory = MutableStateFlow<List<GameDTO>>(emptyList())
    val myGamesHistory = _myGamesHistory.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    /** ----------------------------------------------------------------------- */

    fun fetchAllInfo() {
        fetchMyInfo()
        fetchMyGamesHistory()
    }

    private fun fetchMyInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            _myInfo.value =
                try {
                    me.getMyInfo()
                } catch (e: Exception) {
                    Log.v(TAG, e.toString())
                    val errorMessage = e.toString().split(": ").last()
                    _error.value = errorMessage
                    null
                }
        }
    }

    private fun fetchMyGamesHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            _myGamesHistory.value =
                try {
                    me.getMyGamesHistory()
                } catch (e: Exception) {
                    Log.v(TAG, e.toString())
                    val errorMessage = e.toString().split(": ").last()
                    _error.value = errorMessage
                    emptyList()
                }
        }
    }

    fun resetError() {
        _error.value = null
    }

}


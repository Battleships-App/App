package com.isel.battleshipsAndroid.lobby.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isel.battleshipsAndroid.TAG
import com.isel.battleshipsAndroid.game.model.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * View model for the Lobby Screen hosted by [LobbyActivity]
 */
class LobbyViewModel(
    private val lobbyService: RealLobbyService
) : ViewModel() {

    private val _games = MutableStateFlow<List<Room>>(emptyList())
    val games = _games.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun fetchRooms() {
        viewModelScope.launch(Dispatchers.IO) {
            _games.value =
                try {
                    lobbyService.getRooms()
                } catch (e: Exception) {
                    Log.v(TAG, e.toString())
                    val errorMessage = e.toString().split(": ").last()
                    _error.value = errorMessage
                    emptyList()
                }
        }
    }

    fun joinGame(uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                lobbyService.joinMatch(uuid)
            } catch (e: Exception) {
                Log.v(TAG, e.toString())
                val errorMessage = e.toString().split(": ").last()
                _error.value = errorMessage
            }
        }
    }

    fun createGame(spr: Int, callback: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uuid = lobbyService.createMatch(spr)
                callback(uuid)
            } catch (e: Exception) {
                Log.v(TAG, e.toString())
                val errorMessage = e.toString().split(": ").last()
                _error.value = errorMessage
            }
        }
    }

    fun resetError() {
        _error.value = null
    }

}

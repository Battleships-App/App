package com.isel.battleshipsAndroid.game

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.isel.battleshipsAndroid.DependenciesContainer
import kotlinx.coroutines.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class GameViewModelTests {

    private val app by lazy {
        InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as DependenciesContainer
    }
    /*
    @Test
    fun initial_match_state_is_IDLE() {
        val sut = GameViewModel(app.)
        assertEquals(MatchState.IDLE, sut.state)
    }

     */




}
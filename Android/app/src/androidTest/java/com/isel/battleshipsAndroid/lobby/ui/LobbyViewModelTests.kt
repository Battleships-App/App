package com.isel.battleshipsAndroid.lobby.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.isel.battleshipsAndroid.DependenciesContainer
import com.isel.battleshipsAndroid.lobby.model.LobbyViewModel
import com.isel.battleshipsAndroid.localTestPlayer
import com.isel.battleshipsAndroid.login.model.UserInfo
import com.isel.battleshipsAndroid.login.model.UserInfoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LobbyViewModelTests {

    private val app by lazy {
        InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as DependenciesContainer
    }

}
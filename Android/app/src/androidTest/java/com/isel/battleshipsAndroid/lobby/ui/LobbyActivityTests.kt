package com.isel.battleshipsAndroid.lobby.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.isel.battleshipsAndroid.lobby.LobbyActivity
import com.isel.battleshipsAndroid.ui.NavigateBackTag
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LobbyActivityTests {
    @get:Rule
    val testRule = createAndroidComposeRule<LobbyActivity>()

    @Test
    fun lobby_screen_is_displayed() {
        testRule.onNodeWithTag(LobbyScreenTag).assertExists()
    }

    @Test
    fun pressing_navigate_back_finishes_activity() {

        testRule.onNodeWithTag(NavigateBackTag).assertExists()

        // Act
        testRule.onNodeWithTag(NavigateBackTag).performClick()
        testRule.waitForIdle()

        // Assert
        testRule.onNodeWithTag(LobbyScreenTag).assertDoesNotExist()
        assert(testRule.activityRule.scenario.state == Lifecycle.State.DESTROYED)
    }


}
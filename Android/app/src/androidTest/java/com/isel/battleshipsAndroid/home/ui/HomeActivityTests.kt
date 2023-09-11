package com.isel.battleshipsAndroid.home.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.isel.battleshipsAndroid.BattleshipsApplication
import com.isel.battleshipsAndroid.BattleshipsTestApplication
import com.isel.battleshipsAndroid.home.HomeActivity
import com.isel.battleshipsAndroid.home.HomeScreenTag
import com.isel.battleshipsAndroid.lobby.ui.LobbyScreenTag
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeActivityTests {
    @get:Rule
    val testRule = createAndroidComposeRule<HomeActivity>()

    @Test
    fun screen_contains_play_button() {
        testRule.onNodeWithTag(HomeScreenTag).assertExists()
        testRule.onNodeWithTag("PlayButton").assertExists()
    }

    @Test
    fun screen_contains_profile_button() {
        testRule.onNodeWithTag(HomeScreenTag).assertExists()
        testRule.onNodeWithTag("ProfileButton").assertExists()
    }

    @Test
    fun screen_contains_leaderboard_button() {
        testRule.onNodeWithTag(HomeScreenTag).assertExists()
        testRule.onNodeWithTag("LeaderboardButton").assertExists()
    }

    @Test
    fun screen_contains_exit_button() {
        testRule.onNodeWithTag(HomeScreenTag).assertExists()
        testRule.onNodeWithTag("ExitButton").assertExists()
    }

    @Test
    fun play_button_does_exist_if_user_is_logged_in() {

        // Arrange not required. Default testing repo always returns a UserInfo instance

        testRule.onNodeWithTag(HomeScreenTag).assertExists()
        testRule.onNodeWithTag("PlayButton").assertExists()

    }

    @Test
    fun pressing_play_navigates_to_lobby_if_user_is_logged_in() {

        // Arrange not required. Default testing repo always returns a UserInfo instance

        // Act
        testRule.onNodeWithTag("PlayButton").performClick()
        testRule.waitForIdle()

        // Assert
        testRule.onNodeWithTag(LobbyScreenTag).assertExists()
    }


    @Test
    fun play_button_does_not_exist_if_user_is_not_logged_in() {

        // Arrange
        val testApplication: BattleshipsTestApplication = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as BattleshipsTestApplication

        val defaultUserInfoRepo = testApplication.userInfoRepo

        testApplication.userInfoRepo = mockk {
            every { userInfo } returns null
        }

        try {
            // Assert
            testRule.onNodeWithTag("PlayButton").assertDoesNotExist()
        }
        finally {
            testApplication.userInfoRepo = defaultUserInfoRepo
        }
    }

    @Test
    fun signIn_and_signUp_button_exists_if_user_is_not_logged_in() {

        // Arrange
        val testApplication: BattleshipsTestApplication = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as BattleshipsTestApplication

        val defaultUserInfoRepo = testApplication.userInfoRepo

        testApplication.userInfoRepo = mockk {
            every { userInfo } returns null
        }

        try {
            // Assert
            testRule.onNodeWithTag("SignInAndSignUpButton").assertExists()
            testRule.onNodeWithTag("PlayButton").assertDoesNotExist()
        }
        finally {
            testApplication.userInfoRepo = defaultUserInfoRepo
        }
    }

    @Test
    fun signIn_and_signUp_button_does_not_exist_if_user_is_logged_in() {

        // Arrange
        val testApplication: BattleshipsTestApplication = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as BattleshipsTestApplication

        val defaultUserInfoRepo = testApplication.userInfoRepo

        testApplication.userInfoRepo = mockk {
            every { userInfo } returns null
        }

        try {
            // Assert
            testRule.onNodeWithTag("SignInAndSignUpButton").assertDoesNotExist()
            testRule.onNodeWithTag("PlayButton").assertExists()
        }
        finally {
            testApplication.userInfoRepo = defaultUserInfoRepo
        }
    }


}
package com.isel.battleshipsAndroid.login.ui

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.isel.battleshipsAndroid.login.LoginActivity
import com.isel.battleshipsAndroid.login.model.UserInfo
import com.isel.battleshipsAndroid.login.model.UserInfoRepository
import com.isel.battleshipsAndroid.testutils.PreserveDefaultDependencies
import com.isel.battleshipsAndroid.testutils.createPreserveDefaultDependenciesComposeRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTests {


    @get:Rule
    val testRule = createPreserveDefaultDependenciesComposeRule()


    private val application by lazy {
        (testRule.activityRule as PreserveDefaultDependencies).testApplication
    }


    private val mockRepo: UserInfoRepository = mockk(relaxed = true) {
        val user = slot<UserInfo>()
        every { userInfo = capture(user) } answers { }
        every { userInfo } answers {
            if (user.isCaptured) user.captured else null
        }
    }

    @Test
    fun pressing_signIn_button_stores_info_and_navigates_to_home_destroying_the_login_activity() {

        application.userInfoRepo = mockRepo

        ActivityScenario.launch(LoginActivity::class.java).use {
            testRule.onNodeWithTag(NameInputTag).performTextInput("nick")
            testRule.onNodeWithTag(PasswordInputTag).performTextInput("123")
            testRule.waitForIdle()

            // Assert
            verify { mockRepo.userInfo = UserInfo("nick","test-token") }
            testRule.onNodeWithTag(LoginScreenTag).assertDoesNotExist()
            testRule.onNodeWithTag("PlayButton").assertExists()
            assert(it.state == Lifecycle.State.DESTROYED)
        }
    }




}
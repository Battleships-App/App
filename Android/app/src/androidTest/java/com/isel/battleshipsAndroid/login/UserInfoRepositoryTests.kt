package com.isel.battleshipsAndroid.login

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.isel.battleshipsAndroid.login.UserInfoSharedPrefs
import com.isel.battleshipsAndroid.login.model.UserInfo
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserInfoRepositoryTests {

    private val repo by lazy {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        UserInfoSharedPrefs(context)
    }

    @Test
    fun setting_to_null_clears_userInfo() {
        // Arrange
        repo.userInfo = UserInfo("nick", "moto")
        Assert.assertNotNull(repo.userInfo)

        // Act
        repo.userInfo = null

        // Assert
        Assert.assertNull(repo.userInfo)
    }
}
package com.isel.battleshipsAndroid.user.domain

import com.isel.battleshipsAndroid.login.model.UserInfo
import com.isel.battleshipsAndroid.login.model.userInfoOrNull
import com.isel.battleshipsAndroid.login.model.validateUserInfoParts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UserInfoTests {
    @Test
    fun testUserInfoConstruction() {
        val userInfo = UserInfo("username", "bearer")
        assertEquals("username", userInfo.username)
        assertEquals("bearer", userInfo.bearer)
    }

    @Test
    fun testUserInfoOrNull() {
        val userInfo = userInfoOrNull("username", "bearer")
        assertEquals("username", userInfo?.username)
        assertEquals("bearer", userInfo?.bearer)

        val nullUserInfo = userInfoOrNull("", "")
        assertNull(nullUserInfo)
    }

    @Test
    fun testValidateUserInfoParts() {
        assert(validateUserInfoParts("username", "bearer"))
        assert(!validateUserInfoParts("username", ""))
        assert(!validateUserInfoParts("", "bearer"))
        assert(!validateUserInfoParts("", ""))
    }
}
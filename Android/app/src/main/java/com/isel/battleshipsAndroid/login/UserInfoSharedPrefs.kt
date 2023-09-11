package com.isel.battleshipsAndroid.login

import android.content.Context
import com.isel.battleshipsAndroid.login.model.UserInfo
import com.isel.battleshipsAndroid.login.model.UserInfoRepository

/**
 * A user information repository implementation supported in shared preferences
 */
class UserInfoSharedPrefs(private val context: Context): UserInfoRepository {

    private val userUsernameKey = "Username"
    private val userBearerKey = "Bearer"

    private val prefs by lazy {
        context.getSharedPreferences("UserInfoPrefs", Context.MODE_PRIVATE)
    }

    override var userInfo: UserInfo?
        get() {
            val savedUsername = prefs.getString(userUsernameKey, null)
            val savedBearer = prefs.getString(userBearerKey,null)
            return if (savedUsername != null && savedBearer != null)
                UserInfo(savedUsername, savedBearer)
            else
                null
        }

        set(value) {
            if (value == null)
                prefs.edit()
                    .remove(userUsernameKey)
                    .remove(userBearerKey)
                    .apply()
            else
                prefs.edit()
                    .putString(userUsernameKey, value.username)
                    .putString(userBearerKey, value.bearer)
                    .apply()
        }
}
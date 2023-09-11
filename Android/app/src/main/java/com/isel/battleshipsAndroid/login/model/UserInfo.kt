package com.isel.battleshipsAndroid.login.model


data class UserInfo(val username: String, val bearer: String) {
    init {
        require(validateUserInfoParts(username, bearer))
    }
}

fun userInfoOrNull(username: String, bearer: String): UserInfo? =
    if (validateUserInfoParts(username, bearer))
        UserInfo(username, bearer)
    else
        null

fun validateUserInfoParts(username: String, bearer: String) =
    (username.isNotBlank() && bearer.isNotBlank() && bearer != "null") ?: true

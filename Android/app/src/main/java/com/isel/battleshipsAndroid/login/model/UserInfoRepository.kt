package com.isel.battleshipsAndroid.login.model

/**
 * Contract for user information repository implementations.
 */
interface UserInfoRepository {

    /**
     * The user information, if already stored, or null otherwise. Accesses to
     * this property CAN be made on the main thread (a.k.a. UI thread)
     */
    var userInfo: UserInfo?
}


package com.isel.battleshipsAndroid.me.model

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.isel.battleshipsAndroid.HOST
import com.isel.battleshipsAndroid.game.model.GameDTO
import com.isel.battleshipsAndroid.leaderboard.model.PlayerInfo
import com.isel.battleshipsAndroid.login.model.UserInfo
import com.isel.battleshipsAndroid.utils.hypermedia.ApplicationJsonType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type

interface MeService {

    suspend fun getMyInfo(): PlayerInfo

    suspend fun getMyGamesHistory(): List<GameDTO>

}

class RealMeService(
    private val userInfo: UserInfo,
    httpClient: OkHttpClient,
    private val jsonEncoder: Gson
) : MeService {

    private val client = httpClient.newBuilder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()

            val builder = originalRequest.newBuilder()
                .header("Authorization", "Bearer ${userInfo.bearer}")

            val newRequest = builder.build()
            chain.proceed(newRequest)
        }.build()

    override suspend fun getMyInfo(): PlayerInfo {
        val request = Request.Builder()
            .url("$HOST/players/player?username=${userInfo.username}")
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            return handleResponse(response, PlayerInfo::class.java)
        }
    }

    override suspend fun getMyGamesHistory(): List<GameDTO> {
        val request = Request.Builder()
            .url("$HOST/me/history")
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            return handleResponse<Array<GameDTO>>(response,Array<GameDTO>::class.java).map{it}.reversed()
        }
    }


    /**
     * This method's usefulness is circumstantial. In more realistic scenarios
     * we will not be handling API responses with this simplistic approach.
     */
    private fun <T> handleResponse(response: Response, type: Type): T {
        val contentType = response.body?.contentType()
        return if (response.isSuccessful && contentType != null && contentType == ApplicationJsonType) {
            try {
                val body = response.body?.string()
                jsonEncoder.fromJson<T>(body, type)
            } catch (e: JsonSyntaxException) {
                throw UnexpectedResponseException(response)
            }
        } else {
            val body = response.body?.string()
            throw ResponseException(body.orEmpty())
        }
    }

    abstract class ApiException(msg: String) : Exception(msg)

    /**
     * Exception throw when an unexpected response was received from the API.
     */
    class UnexpectedResponseException(
        val response: Response? = null
    ) : ApiException("Unexpected ${response?.code} response from the API.")

    class ResponseException(
        response: String
    ) : ApiException(response)

}
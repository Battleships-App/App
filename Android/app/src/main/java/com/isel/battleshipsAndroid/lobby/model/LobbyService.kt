package com.isel.battleshipsAndroid.lobby.model


import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.isel.battleshipsAndroid.HOST
import com.isel.battleshipsAndroid.game.model.GameIdModel
import com.isel.battleshipsAndroid.game.model.Room
import com.isel.battleshipsAndroid.login.model.UserInfo
import com.isel.battleshipsAndroid.utils.hypermedia.ApplicationJsonType
import com.isel.battleshipsAndroid.utils.hypermedia.SirenEntity
import com.isel.battleshipsAndroid.utils.hypermedia.SirenMediaType
import okhttp3.*
import okhttp3.internal.EMPTY_REQUEST
import java.lang.reflect.Type

interface LobbyService {

    suspend fun getRooms(): List<Room>

    suspend fun joinMatch(gameId: String): String

    suspend fun createMatch(spr: Int): String

}

class RealLobbyService(
    private val userInfo: UserInfo,
    httpClient: OkHttpClient,
    private val jsonEncoder: Gson
): LobbyService {

    private val client = httpClient.newBuilder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()

            val builder = originalRequest.newBuilder()
                .header("Authorization", "Bearer ${userInfo.bearer}")

            val newRequest = builder.build()
            chain.proceed(newRequest)
        }.build()

    override suspend fun getRooms(): List<Room> {
        val request = Request.Builder()
            .url("$HOST/game/")
            .build()
        client.newCall(request).execute().use { response ->
            val properties = handleResponse<SirenEntity<Room>>(response, SirenEntity::class.java).properties.toString()
            return jsonEncoder.fromJson(properties, Array<Room>::class.java).filter { it.player1 != userInfo.username }
        }
    }

    override suspend fun joinMatch(gameId: String): String {
        val request = Request.Builder()
            .url("$HOST/game/join/?gameId=$gameId")
            .put(EMPTY_REQUEST)
            .build()

        client.newCall(request).execute().use { response ->
            return handleResponse<GameIdModel>(response, GameIdModel::class.java).gameId
        }
    }

    override suspend fun createMatch(spr: Int): String {
        val request = Request.Builder()
            .url("$HOST/game/create?shotsPerRound=$spr")
            .post(EMPTY_REQUEST)
            .build()
        client.newCall(request).execute().use { response ->
            return handleResponse<GameIdModel>(response, GameIdModel::class.java).gameId
        }
    }

    /**
     * This method's usefulness is circumstantial. In more realistic scenarios
     * we will not be handling API responses with this simplistic approach.
     */
    private fun <T> handleResponse(response: Response, type: Type): T {
        val contentType = response.body?.contentType()
        return if (response.isSuccessful && response.body != null && (contentType == ApplicationJsonType || contentType == SirenMediaType)) {
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
        response: Response
    ) : ApiException("Unexpected ${response.code} response from the API.")

    class ResponseException(
        response: String
    ) : ApiException(response)


}

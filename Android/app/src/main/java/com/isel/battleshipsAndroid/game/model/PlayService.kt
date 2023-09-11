package com.isel.battleshipsAndroid.game.model

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.isel.battleshipsAndroid.HOST
import com.isel.battleshipsAndroid.login.model.UserInfo
import com.isel.battleshipsAndroid.utils.hypermedia.ApplicationJsonType
import com.isel.battleshipsAndroid.utils.hypermedia.SirenMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.EMPTY_REQUEST
import java.lang.reflect.Type

interface PlayService {

    val client: OkHttpClient

    suspend fun forfeitMatch(gameId: String)

    suspend fun gameStatus(gameId: String): String

    suspend fun buildBoard(gameId: String, shipsPlacement: Array<String>)

    suspend fun refreshedGameState(gameId: String): GameLastState

    suspend fun makeMove(gameId: String, shotsCoordinates: Array<String>): Boolean

    suspend fun getSecondPlayer(gameId: String): String
}

class RealPlayService(
    private val userInfo: UserInfo,
    private val httpClient: OkHttpClient,
    private val jsonEncoder: Gson,
) : PlayService {
    /**
    This interceptor adds authorization bearer token header to every request regarding games API
     */
    override val client = httpClient.newBuilder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()

            val builder = originalRequest.newBuilder()
                .header("Authorization", "Bearer ${userInfo.bearer}")

            val newRequest = builder.build()
            chain.proceed(newRequest)
        }.build()

    override suspend fun forfeitMatch(gameId: String) {
        val request = Request.Builder()
            .url("$HOST/game/forfeit?gameId=$gameId")
            .put(EMPTY_REQUEST)
            .build()

        client.newCall(request).execute().use { response ->
            handleResponse<GameBoard>(response, GameIdModel::class.java)
        }
    }

    override suspend fun gameStatus(gameId: String): String {
        val request = Request.Builder()
            .url("$HOST/game/status?gameId=$gameId")
            .build()

        client.newCall(request).execute().use { response ->
            return handleResponse<State>(response, State::class.java).status
        }
    }

    override suspend fun buildBoard(gameId: String, shipsPlacement: Array<String>) {
        val body = (jsonEncoder.toJson(shipsPlacement)).toRequestBody(ApplicationJsonType)

        val request = Request.Builder()
            .url("$HOST/game/build?gameId=$gameId")
            .put(body)
            .build()

        client.newCall(request).execute().use { response ->
            handleResponse<GameBoard>(response, GameBoard::class.java)
        }
    }

    override suspend fun refreshedGameState(gameId: String): GameLastState {
        val request = Request.Builder()
            .url("$HOST/game/refresh?gameId=$gameId")
            .build()

        client.newCall(request).execute().use { response ->
            return handleResponse(response, GameLastState::class.java)
        }
    }

    override suspend fun makeMove(gameId: String, shotsCoordinates: Array<String>): Boolean {
        val body = (jsonEncoder.toJson(shotsCoordinates)).toRequestBody(ApplicationJsonType)

        val request = Request.Builder()
            .url("$HOST/game/play?gameId=$gameId")
            .put(body)
            .build()

        client.newCall(request).execute().use { response ->
            return handleResponse<HitOrMiss>(response, HitOrMiss::class.java)
                .hitOrMiss.first()
        }
    }

    override suspend fun getSecondPlayer(gameId: String): String {
        val request = Request.Builder()
            .url("$HOST/me/running")
            .build()

        client.newCall(request).execute().use { response ->
            return handleResponse<Array<GameDTO>>(response, Array<GameDTO>::class.java)
                .find { it.uuid == gameId }?.player2 ?: "waiting..."
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
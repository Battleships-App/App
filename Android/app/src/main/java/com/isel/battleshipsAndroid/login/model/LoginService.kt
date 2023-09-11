package com.isel.battleshipsAndroid.login.model

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.isel.battleshipsAndroid.HOST
import com.isel.battleshipsAndroid.utils.hypermedia.ApplicationJsonType
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type

interface LoginService {

    suspend fun login(username: String, password: String): Token

    suspend fun register(username: String, password: String): Token
}

class RealLoginService(
    private val httpClient: OkHttpClient,
    private val jsonEncoder: Gson
) : LoginService {


    override suspend fun login(username: String, password: String): Token {
        val body = ("{" +
                "\"username\": \"$username\"," +
                "\"password\": \"$password\"" +
                "}").toRequestBody(ApplicationJsonType)
        val request = Request.Builder()
            .url("$HOST/players/signin")
            .post(body)
            .build()
        httpClient.newCall(request).execute().use { response ->
            return handleResponse(response, Token::class.java)
        }
    }

    override suspend fun register(username: String, password: String): Token {
        val body = ("{" +
                "\"username\": \"$username\"," +
                "\"password\": \"$password\"" +
                "}").toRequestBody(ApplicationJsonType)
        val request = Request.Builder()
            .url("$HOST/players/signup")
            .post(body)
            .build()
        httpClient.newCall(request).execute().use { response ->
            return handleResponse(response, Token::class.java)
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
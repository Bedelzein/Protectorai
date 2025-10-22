package kz.protectorai.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kz.protectorai.core.EMPTY_STRING
import kz.protectorai.core.Payload

object GuestRepository {
    private val http by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                        explicitNulls = false
                    }
                )
            }
            defaultRequest { url(BASE_URL) }
        }
    }

    suspend fun auth(username: String, password: String): Payload<AuthResponseBody> {
        val res = http.submitForm(
            url = "auth/login",
            formParameters = Parameters.build {
                append("username", username)
                append("password", password)
                append("grant_type", "password")
                append("scope", EMPTY_STRING)
                append("client_id", "null")
                append("client_secret", "null")
            }
        )
        try {
            return Payload.Success(res.body())
        } catch (_: Throwable) {
            try {
                val message = res.body<AuthErrorResponse>().detail
                return Payload.Failure(message)
            } catch (e: Throwable) {
                return Payload.Failure(e.message ?: e.toString())
            }
        }
    }
}

@Serializable
data class AuthResponseBody(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String
)

@Serializable
data class AuthErrorResponse(val detail: String)
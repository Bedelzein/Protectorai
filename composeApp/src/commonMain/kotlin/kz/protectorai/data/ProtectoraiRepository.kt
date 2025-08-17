package kz.protectorai.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.parametersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kz.protectorai.CommonHardcode
import kz.protectorai.core.EMPTY_STRING
import kz.protectorai.navigation.feed.GetIncidentsRequestBody
import kz.protectorai.navigation.feed.Incident

private const val BASE_URL = "https://api.protectorai.kz"

class ProtectoraiRepository {

    private val token = CommonHardcode
        .wildcard { "0lYcXOwyQwuu/8D?fGRP2CthOn9r?wfS5sg4awCdlPL7a3nHYGJG?Cq=M7USCANqMwAIoeO3Rd-QyE8X099OjD9k8g2lHk5DGjkHcgC!J33sg8ILwNRYI/bJ?p9OgWaB/zEZSxNYhq6L7CZHcl30Jb7k8n9nwQi!xqNbZmhTFpTU3NGAq?dgbYX2ZHyJ-FFLSdrViagDlk40OYBxYlQgc=bAzLVeE=W!wiz!baUKBw2WTzXXIvzjAGTtT0smuApw" }

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
        }
    }

    suspend fun auth(username: String, password: String): AuthResponseBody = http.submitForm(
        url = "$BASE_URL/auth/login",
        formParameters = Parameters.build {
            append("username", username)
            append("password", password)
            append("grant_type", "password")
            append("scope", EMPTY_STRING)
            append("client_id", "null")
            append("client_secret", "null")
        }
    ) { headers { append("X-API-Key", token) } }.body()

    suspend fun getIncidentTypes(): Map<String, String> = http.post("$BASE_URL/event_types/get") {
        headers { append("X-API-Key", token) }
    }.body()

    suspend fun getLocations(): List<String> = http.submitForm(
        url = "${BASE_URL}/locations/get",
        formParameters = parametersOf("username", "test_user")
    ) { headers { append("X-API-Key", token) } }.body()

    suspend fun getIncidents(
        request: GetIncidentsRequestBody
    ) = http.post("$BASE_URL/events/get") {
        contentType(ContentType.Application.Json)
        headers { append("X-API-Key", token) }
        setBody(request)
    }.body<List<Incident>>()
}

@Serializable
data class AuthResponseBody(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String
)
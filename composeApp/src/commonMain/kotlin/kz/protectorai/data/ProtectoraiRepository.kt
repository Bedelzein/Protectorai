package kz.protectorai.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.http.parametersOf
import io.ktor.serialization.kotlinx.json.json
import kz.protectorai.CommonHardcode

private const val BASE_URL = "https://api.protectorai.kz"

class ProtectoraiRepository {

    private val token = CommonHardcode
        .wildcard { "GKkdW80eGzrGrwAGw9PsjSVh7qWKZD8MB9AEzjSAiYJMpegAE6sSwB8OwWhpsGbh" }

    private val http by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    suspend fun getIncidentTypes(): Map<String, String> = http.post("${BASE_URL}/event_types/get") {
        headers { append("X-API-Key", token) }
    }.body()

    suspend fun getLocations(): List<String> = http.submitForm(
        url = "${BASE_URL}/locations/get",
        formParameters = parametersOf("username", "test_user")
    ) { headers { append("X-API-Key", token) } }.body()
}
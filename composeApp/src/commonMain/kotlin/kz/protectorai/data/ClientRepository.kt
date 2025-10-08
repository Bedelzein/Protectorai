package kz.protectorai.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.delete
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.parametersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.locks.synchronized
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kz.protectorai.navigation.feed.GetIncidentsRequestBody
import kz.protectorai.navigation.feed.Incident
import kz.protectorai.util.FirebaseUtil

class ClientRepository private constructor(accessToken: String) {

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
            defaultRequest {
                headers { append("Authorization", "Bearer $accessToken") }
                url(BASE_URL)
            }
        }
    }

    suspend fun registerFirebaseToken(
        deviceId: String,
        firebaseToken: String
    ) = http.post("device/firebase_token") {
        contentType(ContentType.Application.Json)
        setBody(
            mapOf(
                "device_id" to deviceId,
                "firebase_token" to firebaseToken
            )
        )
    }

    suspend fun unregisterFirebaseToken(
        deviceId: String
    ) = http.delete("device/firebase_token/delete") {
        contentType(ContentType.Application.Json)
        setBody(mapOf("device_id" to deviceId))
    }

    suspend fun getIncidentTypes(): Map<String, String> = http.post("event_types/get").body()

    suspend fun getLocations(): List<String> = http.submitForm(
        url = "locations/get",
        formParameters = parametersOf("username", "test_user")
    ).body()

    suspend fun getIncidents(
        request: GetIncidentsRequestBody
    ) = http.post("events/get") {
        contentType(ContentType.Application.Json)
        setBody(request)
    }.body<List<Incident>>()

    companion object Companion {

        private var instance: ClientRepository? = null

        fun getInstance(accessToken: String) = instance
            ?: ClientRepository(accessToken).also { instance = it }

        fun getInstanceUnsafe() = instance ?: error("ClientRepository is not initialized")

        fun logout() {
            FirebaseUtil.default.unregisterFirebaseToken()
            instance = null
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
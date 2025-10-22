package kz.protectorai.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.delete
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.parametersOf
import io.ktor.serialization.kotlinx.json.json
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

    suspend fun getIncidentClasses(): List<Incident.Type> = http.get("event-classes").body()

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

    suspend fun getClientInfo(): ClientInfo = http.get("auth/me").body()

    suspend fun getInstitutions(
        companyId: String
    ): List<Institution> = http.get("companies/$companyId/institutions").body()

    companion object Companion {

        private var instance: ClientRepository? = null

        fun getInstance(accessToken: String) = instance
            ?: ClientRepository(accessToken).also { instance = it }

        fun getInstanceUnsafe() = instance ?: error("ClientRepository is not initialized")

        fun logout() {
            try {
                FirebaseUtil.default.unregisterFirebaseToken()
            } catch (e: Exception) {

            }
            instance = null
        }
    }
}

@Serializable
data class ClientInfo(
    val username: String,

    @SerialName("company_id")
    val companyId: String,

    @SerialName("available_locations")
    val availableLocations: List<String>,

    val roles: List<String>
)

@Serializable
data class Institution(
    @SerialName("institution_id")
    val id: String,

    val name: String,

    @SerialName("company_id")
    val companyId: String,

    val address: String?
)
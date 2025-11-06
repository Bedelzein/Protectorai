package kz.protectorai.navigation.feed

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Stable
@Serializable
data class IncidentsResponse(
    val items: List<Incident>,
    val total: Int,
    val page: Int,
    val size: Int,
    val pages: Int
)

@Immutable
@Stable
@Serializable
data class Incident(
    val id: String,
    @SerialName("event_type")
    val eventType: String,
    @SerialName("video_source")
    val videoSource: String,
    @SerialName("add_information")
    val addInformation: String,
    @SerialName("location_id")
    val locationId: String,
    @SerialName("time")
    val time: String,
    @SerialName("camera_id")
    val cameraId: String,
    @SerialName("probability")
    val probability: Float,
    @SerialName("confirmed")
    val isConfirmed: Boolean,
    @SerialName("class_id")
    val classId: Int
) {

    @Serializable
    data class Type(
        val id: Int,
        val name: String,
        val description: String
    )
}
package kz.protectorai.navigation.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val isConfirmed: Boolean
) {

    @Serializable
    data class Type(
        val id: Int,
        val name: String,
        val description: String
    )
}
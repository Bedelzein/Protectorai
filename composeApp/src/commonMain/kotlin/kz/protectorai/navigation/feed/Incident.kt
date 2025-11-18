package kz.protectorai.navigation.feed

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char
import kotlinx.datetime.serializers.FormattedInstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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

@OptIn(ExperimentalTime::class)
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
    @Serializable(with = TimeSerializer::class)
    val time: Instant,
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

    private class TimeSerializer : FormattedInstantSerializer(
        name = TimeSerializer::class.qualifiedName ?: "TimeSerializer",
        format = DateTimeComponents.Format {
            year()
            /** TODO: the original format had an `y` directive, so the behavior is different on years earlier than 1 AD. See the [kotlinx.datetime.format.byUnicodePattern] documentation for details. */
            char('-')
            monthNumber()
            char('-')
            day()
            char('T')
            hour()
            char(':')
            minute()
            char(':')
            second()
        }
    )
}
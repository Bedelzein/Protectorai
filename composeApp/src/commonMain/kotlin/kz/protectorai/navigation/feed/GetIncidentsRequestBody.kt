package kz.protectorai.navigation.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetIncidentsRequestBody(
    @SerialName("is_confirmed")
    val isConfirmed: Boolean,
    @SerialName("location_ids")
    val locationIds: List<String>,
    @SerialName("event_types")
    val eventTypes: List<String>,
    @SerialName("start_date")
    val startDate: String,
    @SerialName("end_date")
    val endDate: String
)
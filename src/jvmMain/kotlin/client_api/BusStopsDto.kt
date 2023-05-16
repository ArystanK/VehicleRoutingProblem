package client_api

import kotlinx.serialization.Serializable

@Serializable
data class BusStopsDto(
    val features: List<Feature>,
    val properties: PropertiesX,
    val type: String
)
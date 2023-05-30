package data.client_api

import kotlinx.serialization.Serializable

@Serializable
data class Feature(
    val geometry: Geometry,
    val properties: Properties = Properties(),
    val type: String = "",
)
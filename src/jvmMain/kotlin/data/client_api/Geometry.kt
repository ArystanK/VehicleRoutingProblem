package data.client_api

import kotlinx.serialization.Serializable

@Serializable
data class Geometry(
    val coordinates: List<Double>,
    val type: String
)
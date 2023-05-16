package client_api

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val boundedBy: List<List<Double>>,
    val display: String,
    val found: Int
)
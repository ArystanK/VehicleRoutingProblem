package client_api

import kotlinx.serialization.Serializable

@Serializable
data class SearchRequest(
    val boundedBy: List<List<Double>>,
    val request: String,
    val results: Int,
    val skip: Int
)
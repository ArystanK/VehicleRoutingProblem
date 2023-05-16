package client_api

import kotlinx.serialization.Serializable

@Serializable
data class ResponseMetaData(
    val SearchRequest: SearchRequest,
    val SearchResponse: SearchResponse
)
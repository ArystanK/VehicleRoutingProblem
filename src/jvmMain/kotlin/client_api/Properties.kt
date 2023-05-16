package client_api

import kotlinx.serialization.Serializable

@Serializable
data class Properties(
    val CompanyMetaData: CompanyMetaData,
    val boundedBy: List<List<Double>>,
    val description: String,
    val name: String
)
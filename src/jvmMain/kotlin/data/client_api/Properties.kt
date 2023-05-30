package data.client_api

import kotlinx.serialization.Serializable

@Serializable
data class Properties(
    val CompanyMetaData: CompanyMetaData = CompanyMetaData(),
    val boundedBy: List<List<Double>> = listOf(),
    val description: String = "",
    val name: String = "",
)
package data.client_api

import kotlinx.serialization.Serializable

@Serializable
data class CompanyMetaData(
    val Categories: List<Category> = listOf(),
    val address: String = "",
    val id: String = "",
    val name: String = "",
    val url: String? = null,
)
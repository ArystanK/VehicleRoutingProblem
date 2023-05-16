package client_api

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val `class`: String,
    val name: String
)
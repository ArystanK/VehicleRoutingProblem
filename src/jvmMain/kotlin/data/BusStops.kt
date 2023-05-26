package data

@kotlinx.serialization.Serializable
data class BusStop(
    val id: Int,
    val lat: Double,
    val lon: Double,
    val address: String,
)


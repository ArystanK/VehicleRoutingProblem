import org.jetbrains.exposed.dao.id.IntIdTable

@kotlinx.serialization.Serializable
data class BusStop(
    val id: Int,
    val lat: Double,
    val lon: Double,
    val address: String,
)

object BusStopTable : IntIdTable("bus_stops") {
    val lat = double("lat")
    val lon = double("lon")
    val address = text("address")
}
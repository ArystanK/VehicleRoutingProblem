package data.client_api

import center.sciprog.maps.coordinates.Gmc
import center.sciprog.maps.features.Rectangle
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


object BusStopsClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun getBusStops(searchBox: Rectangle<Gmc>): BusStopsDto {
        val request =
            "https://search-maps.yandex.ru/v1/" +
                    "?apikey=1a18a6ef-f125-40ad-b5e0-ff8c415455bf" +
                    "&text=public%20transport%20stop" +
                    "&lang=en_KZ" +
                    "&bbox=${searchBox.a.longitude.toDegrees().value},${searchBox.a.latitude.toDegrees().value}~${searchBox.b.longitude.toDegrees().value},${searchBox.b.latitude.toDegrees().value}" +
                    "&rspn=1" +
                    "&results=500"
        val response = client.get(request)
        return response.body()
    }
}


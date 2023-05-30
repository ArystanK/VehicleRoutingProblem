package presentation

sealed interface VisualizationIntent {
    data class RouteShowIntent(val routeId: Int) : VisualizationIntent

    data class BusStopsPickIntent(val busStopsId: Int) : VisualizationIntent

}
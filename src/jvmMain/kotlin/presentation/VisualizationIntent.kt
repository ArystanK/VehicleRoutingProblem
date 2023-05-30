package presentation

sealed interface VisualizationIntent {
    data class RouteShowIntent(val routeId: Int) : VisualizationIntent

}
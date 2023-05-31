package presentation

import center.sciprog.maps.coordinates.Gmc
import center.sciprog.maps.features.Rectangle
import domain.poko.BusStops
import domain.poko.RouteList
import domain.poko.SolutionMethod

sealed interface VisualizationIntent {
    @JvmInline
    value class RouteShowIntent(val routeId: Int) : VisualizationIntent {
        override fun toString(): String = "RouteShowIntent(routeId=$routeId)"
    }

    @JvmInline
    value class BusStopsPickIntent(val busStops: BusStops) : VisualizationIntent {
        override fun toString(): String = "BusStopsPickIntent(busStops=$busStops)"
    }

    @JvmInline
    value class RouteListPickIntent(val routeList: RouteList?) : VisualizationIntent {
        override fun toString(): String = "RouteListPickIntent(routeList=$routeList)"
    }

    object BusStopsAddModeToggle : VisualizationIntent

    data class BusStopsAddIntent(val searchBox: Rectangle<Gmc>) : VisualizationIntent

    object RoutesAddModeToggle : VisualizationIntent

    data class RoutesAdd(val numberOfRoutes: Int, val method: SolutionMethod) : VisualizationIntent
}
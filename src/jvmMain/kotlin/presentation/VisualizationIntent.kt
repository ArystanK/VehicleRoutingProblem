package presentation

import center.sciprog.maps.coordinates.Gmc
import center.sciprog.maps.features.Rectangle
import domain.poko.*

sealed interface VisualizationIntent {
    @JvmInline
    value class RouteShowIntent(val route: List<BusStop>) : VisualizationIntent {
        override fun toString(): String = "RouteShowIntent(route=$route)"
    }

    @JvmInline
    value class BusStopsPickIntent(val busStops: BusStops) : VisualizationIntent {
        override fun toString(): String = "BusStopsPickIntent(busStops=$busStops)"
    }

    @JvmInline
    value class RouteListPickIntent(val route: Route?) : VisualizationIntent {
        override fun toString(): String = "RouteListPickIntent(routeList=$route)"
    }

    object BusStopsAddModeToggle : VisualizationIntent

    data class BusStopsAddIntent(val searchBox: Rectangle<Gmc>) : VisualizationIntent

    object RoutesAddModeToggle : VisualizationIntent

    object RoutesAdd : VisualizationIntent

    @JvmInline
    value class SolutionMethodChangeIntent(val solutionMethod: SolutionMethod) : VisualizationIntent {
        override fun toString(): String {
            return "SolutionMethodChangeIntent(solutionMethod=$solutionMethod)"
        }
    }

    @JvmInline
    value class NumberOfRouteChangeIntent(val numberOfRoutes: String) : VisualizationIntent
}
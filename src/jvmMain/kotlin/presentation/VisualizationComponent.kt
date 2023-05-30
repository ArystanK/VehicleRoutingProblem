package presentation

import domain.repository.BusStopsRepository
import domain.repository.RoutesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class VisualizationComponent(
    private val routesRepository: RoutesRepository,
    private val busStopsRepository: BusStopsRepository,
) {
    private val _state = MutableStateFlow(VisualizationState())
    val state = _state.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            routesRepository.getRoutes().onSuccess { routes ->
                busStopsRepository.getBusStops().onSuccess { busStops ->
                    _state.update {
                        it.copy(
                            busStops = busStops.groupBy { it.busStops },
                            routes = routes.groupBy { it.routes }
                        )
                    }

                }
            }

        }
    }

    fun reduce(intent: VisualizationIntent) {
        when (intent) {
            is VisualizationIntent.RouteShowIntent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    routesRepository.getRouteById(intent.routeId).onSuccess {
                        _state.update { state ->
                            val isRouteListAlreadySelected = it in state.routesShown
                            val newRoutesShown =
                                if (!isRouteListAlreadySelected) state.routesShown + it else state.routesShown - it
                            state.copy(
                                routesShown = newRoutesShown
                            )
                        }
                    }

                }
            }

            is VisualizationIntent.BusStopsPickIntent -> {
                _state.update {
                    it.copy(busStopsId = intent.busStopsId)
                }
            }
        }
    }
}
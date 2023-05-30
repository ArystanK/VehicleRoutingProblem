package presentation

import domain.repository.RoutesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class VisualizationComponent(
    private val routesRepository: RoutesRepository,
) {
    private val _state = MutableStateFlow(VisualizationState())
    val state = _state.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val routes = routesRepository.getRoutes().groupBy { it.routes }
            _state.update {
                it.copy(
                )
            }
        }
    }

    fun reduce(intent: VisualizationIntent) {
        when (intent) {
            is VisualizationIntent.RouteShowIntent -> {
                _state.update { state ->
                    state.copy(
                        routesShown = if (intent.routeId in state.routesShown)
                            state.routesShown - intent.routeId
                        else state.routesShown + intent.routeId
                    )
                }
            }
        }
    }
}
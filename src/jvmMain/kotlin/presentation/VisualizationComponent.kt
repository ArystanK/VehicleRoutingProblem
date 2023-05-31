package presentation

import domain.genetic_algorithm.VehicleRoutingProblemGeneticAlgorithm
import domain.linear_programming.solveVRPLinearProgramming
import domain.linear_programming.toRoute
import domain.poko.*
import domain.repository.BusStopsRepository
import domain.repository.FitnessRepository
import domain.repository.RoutesRepository
import generateDistanceMatrix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import manhattanDistance


class VisualizationComponent(
    private val routesRepository: RoutesRepository,
    private val busStopsRepository: BusStopsRepository,
    private val fitnessRepository: FitnessRepository,
) {
    private val _state = MutableStateFlow(VisualizationState())
    val state = _state.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            routesRepository.getRoutes().onSuccess { routes ->
                busStopsRepository.getAllBusStops().onSuccess { busStops ->
                    val groupedByBusStops = busStops.groupBy { it.busStops }
                    _state.update {
                        it.copy(
                            busStops = groupedByBusStops,
                            routes = routes.groupBy { it.routes }
                        )
                    }
                }
            }
        }
    }

    fun reduce(intent: VisualizationIntent) {
        println(intent.toString())
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
                    it.copy(busStopsKey = intent.busStops)
                }
            }

            is VisualizationIntent.RouteListPickIntent -> {
                _state.update {
                    it.copy(routeListKey = intent.routeList)
                }
            }

            is VisualizationIntent.BusStopsAddIntent -> {
                if (!state.value.isBusStopsAddMode) return
                _state.update { it.copy(isBusStopsAddMode = false) }
                CoroutineScope(Dispatchers.IO).launch {
                    busStopsRepository.addBusStops(intent.searchBox).onSuccess { busStops ->
                        _state.update {
                            it.copy(busStops = it.busStops + busStops.groupBy { it.busStops })
                        }
                    }

                }
            }

            VisualizationIntent.BusStopsAddModeToggle -> {
                _state.update {
                    it.copy(isBusStopsAddMode = !it.isBusStopsAddMode)
                }
            }

            VisualizationIntent.RoutesAddModeToggle -> {
                _state.update {
                    it.copy(isAddRoutesMode = !it.isAddRoutesMode)
                }
            }

            is VisualizationIntent.RoutesAdd -> {
                CoroutineScope(Dispatchers.IO).launch {
                    _state.update {
                        val numberOfRoutes = it.addRoutesState.numberOfRoutes.toIntOrNull() ?: return@launch
                        if (!it.isAddRoutesMode) return@launch
                        val busStops = it.busStops[it.busStopsKey] ?: return@launch
                        val distanceMatrix = generateDistanceMatrix(
                            busStops = busStops,
                            distance = { first, second ->
                                manhattanDistance(first.lat to first.lon, second.lat to second.lon)
                            }
                        )
                        val routes = async(Dispatchers.Main) {
                            when (it.addRoutesState.solutionMethod) {
                                SolutionMethod.LP -> {
                                    solveVRPLinearProgramming(
                                        numberOfRoutes = numberOfRoutes,
                                        distMatrix = distanceMatrix
                                    ).map { it.toRoute() }.also { println(it) }
                                }

                                SolutionMethod.GA -> {
                                    VehicleRoutingProblemGeneticAlgorithm(
                                        distMatrix = distanceMatrix,
                                        numberOfRoutes = numberOfRoutes,
                                        fitnessRepository = fitnessRepository,
                                        busStopsId = it.busStopsKey?.id ?: return@async listOf()
                                    ).solve()
                                }

                                SolutionMethod.GNN -> {
                                    listOf()
                                }
                            }
                        }

                        val result =
                            routesRepository.safeSolution(
                                routes = routes.await(),
                                busStopsId = it.busStopsKey?.id ?: return@launch,
                                type = it.addRoutesState.solutionMethod.name,
                                busStopList = it.busStops[it.busStopsKey] ?: return@launch
                            )
                        it.copy(
                            routes = it.routes + (result.getOrDefault(emptyList()).flatten().groupBy { it.routes }),
                            isAddRoutesMode = false
                        )
                    }
                }
            }

            is VisualizationIntent.NumberOfRouteChangeIntent -> {
                _state.update {
                    it.copy(addRoutesState = it.addRoutesState.copy(numberOfRoutes = intent.numberOfRoutes))
                }
            }

            is VisualizationIntent.SolutionMethodChangeIntent -> {
                _state.update {
                    it.copy(addRoutesState = it.addRoutesState.copy(solutionMethod = intent.solutionMethod))
                }
            }
        }
    }
}
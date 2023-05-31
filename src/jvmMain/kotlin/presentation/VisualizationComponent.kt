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
            busStopsRepository.getAllBusStops().onSuccess { busStops ->
                routesRepository.getRoutesList().onSuccess { routeList ->
                    val groupedByBusStops = busStops.groupBy { it.busStops }
                    _state.update {
                        it.copy(
                            busStops = groupedByBusStops,
                            routes = emptyList(),
                            routesList = routeList.groupBy { it.busStops }
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
                    _state.update { state ->
                        val isRouteListAlreadySelected = intent.route in state.routesShown
                        val newRoutesShown = state.routesShown.toMutableList()
                        if (!isRouteListAlreadySelected) newRoutesShown.add(intent.route) else newRoutesShown.remove(intent.route)
                        state.copy(
                            routesShown = newRoutesShown
                        )
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
                    it.copy(routeKey = intent.route)
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
                                        busStops = it.busStopsKey ?: return@async listOf()
                                    ).solve()
                                }

                                SolutionMethod.GNN -> {
                                    listOf()
                                }
                            }.map { it.map { busStops[it] } }
                        }.await()

//                        val result =
//                            routesRepository.safeSolution(
//                                routes = routes,
//                                busStops = it.busStopsKey ?: return@launch,
//                                type = it.addRoutesState.solutionMethod.name
//                            )
                        it.copy(
                            routes = routes,
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
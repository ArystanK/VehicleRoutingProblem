package database

import data.BusStop
import kotlinx.coroutines.coroutineScope
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import toRoute

object Database {
    private fun connectToDatabase() = org.jetbrains.exposed.sql.Database.connect(
        url = "jdbc:postgresql://localhost:5432/vehicle_routing_problem",
        password = "qwerty",
        user = "postgres",
        driver = "org.postgresql.Driver"
    )


    private fun initializeDatabase() = transaction {
        addLogger(StdOutSqlLogger)
    }

    init {
        connectToDatabase()
        initializeDatabase()
    }

    fun saveBusStops(busStops: List<BusStop>) = transaction {
        addLogger(StdOutSqlLogger)
        BusStopTable.batchInsert(busStops) {
            this[BusStopTable.id] = it.id
            this[BusStopTable.latitude] = it.lat
            this[BusStopTable.longitude] = it.lon
            this[BusStopTable.address] = it.address
        }
    }

    fun getBusStops(): List<BusStop> = transaction {
        addLogger(StdOutSqlLogger)
        BusStopTable.selectAll().map {
            BusStop(
                it[BusStopTable.id].value,
                it[BusStopTable.latitude],
                it[BusStopTable.longitude],
                it[BusStopTable.address]
            )
        }
    }

    fun saveFitness(fitness: Pair<Double, Double>, routeId: Int) = transaction {
        addLogger(StdOutSqlLogger)
        Fitness.insert {
            it[maxFitness] = fitness.first
            it[avgFitness] = fitness.second
            it[Fitness.routeId] = routeId
        }
    }

    fun getFitness() = transaction {
        Fitness.selectAll().map { it[Fitness.avgFitness] to it[Fitness.maxFitness] }
    }

    fun saveRoute(route: List<Int>, routesId: Int, source: String) = transaction {
        addLogger(StdOutSqlLogger)
        val newRoute = RoutesList.insert {
            it[sourceColumn] = source
            it[RoutesList.routesId] = routesId
        }
        Route.batchInsert(route) {
            this[Route.busStopId] = it
            this[Route.routeId] = newRoute[RoutesList.id]
        }
    }

    fun getAllRoutes(): Map<Int, List<Int>> = transaction {
        addLogger(StdOutSqlLogger)
        Route.selectAll().groupBy {
            it[Route.routeId].value
        }.map {
            it.key to it.value.map { it[Route.busStopId].value }
        }.toMap()
    }

    fun getRouteByRouteId(routeId: Int): List<Int>? = transaction {
        addLogger(StdOutSqlLogger)
        Route.selectAll().groupBy {
            it[Route.routeId].value
        }[routeId]?.map {
            it[Route.busStopId].value
        }
    }

    fun getLastRouteId(): Int = transaction {
        addLogger(StdOutSqlLogger)
        RoutesList.selectAll().orderBy(RoutesList.id, order = SortOrder.DESC).limit(1)
            .map { it[RoutesList.id].value }.firstOrNull() ?: -1
    }
}

suspend fun saveRouteToDatabase(
    numberOfRoutes: Int,
    distMatrix: Array<DoubleArray>,
    routes: Array<Array<BooleanArray>>,
    source: String,
): Array<Array<BooleanArray>> {
    coroutineScope {
        val prevRouteId = Database.getLastRouteId() + 1
        for (i in prevRouteId until routes.size + prevRouteId) {
            val route = routes[i - prevRouteId].toRoute()
            Database.saveRoute(route, i, source)
        }
    }
    return routes
}



suspend fun saveRouteToDatabase(
    numberOfRoutes: Int,
    distMatrix: Array<DoubleArray>,
    routes: List<List<Int>>,
    source: String,
): List<List<Int>> {
    coroutineScope {
        val prevRouteId = Database.getLastRouteId() + 1
        for (i in prevRouteId until routes.size + prevRouteId)
            Database.saveRoute(routes[i - prevRouteId], i, source)
    }
    return routes
}
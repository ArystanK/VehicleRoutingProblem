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
            this[BusStopTable.lat] = it.lat
            this[BusStopTable.lon] = it.lon
            this[BusStopTable.address] = it.address
        }
    }

    fun getBusStops(): List<BusStop> = transaction {
        addLogger(StdOutSqlLogger)
        BusStopTable.selectAll().map {
            BusStop(
                it[BusStopTable.id].value,
                it[BusStopTable.lat],
                it[BusStopTable.lon],
                it[BusStopTable.address]
            )
        }
    }

    fun saveFitness(fitness: Pair<Double, Double>) = transaction {
        addLogger(StdOutSqlLogger)
        Fitness.insert {
            it[maxFitness] = fitness.first
            it[avgFitness] = fitness.second
        }
    }

    fun getFitness() = transaction {
        Fitness.selectAll().map { it[Fitness.avgFitness] to it[Fitness.maxFitness] }
    }

    fun saveRoute(route: List<Int>, routeId: Int) = transaction {
        addLogger(StdOutSqlLogger)
        RouteTable.batchInsert(route) {
            this[RouteTable.nodeId] = it
            this[RouteTable.routeId] = routeId
        }
    }

    fun getAllRoutes(): Map<Int, List<Int>> = transaction {
        addLogger(StdOutSqlLogger)
        RouteTable.selectAll().groupBy {
            it[RouteTable.routeId]
        }.map {
            it.key to it.value.map { it[RouteTable.nodeId] }
        }.toMap()
    }

    fun getRouteByRouteId(routeId: Int): List<Int>? = transaction {
        addLogger(StdOutSqlLogger)
        RouteTable.selectAll().groupBy {
            it[RouteTable.routeId]
        }[routeId]?.map {
            it[RouteTable.nodeId]
        }
    }

    fun getLastRouteId(): Int = transaction {
        addLogger(StdOutSqlLogger)
        RouteTable.selectAll().orderBy(RouteTable.routeId, order = SortOrder.DESC).limit(1)
            .map { it[RouteTable.routeId] }.firstOrNull() ?: 0
    }
}

suspend fun saveRouteToDatabase(
    numberOfRoutes: Int,
    distMatrix: Array<DoubleArray>,
    routes: Array<Array<BooleanArray>>,
): Array<Array<BooleanArray>> {
    coroutineScope {
        val prevRouteId = Database.getLastRouteId()
        for (i in prevRouteId until routes.size + prevRouteId)
            Database.saveRoute(routes[i - prevRouteId].toRoute(), i)
    }
    return routes
}

suspend fun saveRouteToDatabase(
    numberOfRoutes: Int,
    distMatrix: Array<DoubleArray>,
    routes: List<List<Int>>,
): List<List<Int>> {
    coroutineScope {
        val prevRouteId = Database.getLastRouteId()
        for (i in prevRouteId until routes.size + prevRouteId)
            Database.saveRoute(routes[i - prevRouteId], i)
    }
    return routes
}
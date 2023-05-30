package data.database

import data.BusStop
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

object VRPDatabase {
    private val database: Database by lazy {
        Database.connect(
            url = "jdbc:postgresql://localhost/vrp_database",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "qwerty"
        )
    }

    init {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        setup()
    }

    private fun setup() {
        transaction(database) {
            if (!BusStopsTable.exists()) SchemaUtils.create(BusStopsTable)
            if (!BusStopTable.exists()) SchemaUtils.create(BusStopTable)
            if (!FitnessListTable.exists()) SchemaUtils.create(FitnessListTable)
            if (!FitnessTable.exists()) SchemaUtils.create(FitnessTable)
            if (!RouteListTable.exists()) SchemaUtils.create(RouteListTable)
            if (!RouteTable.exists()) SchemaUtils.create(RouteTable)
        }
    }

    fun createBusStops(): BusStopsEntity = transaction(database) { BusStopsEntity.new { } }

    fun getBusStopsById(id: Int): BusStopsEntity? =
        transaction(database) { BusStopsEntity.findById(id) }

    fun getAllBusStopCollections(): List<BusStopsEntity> =
        transaction(database) { BusStopsEntity.all().toList() }

    fun deleteBusStops(id: Int) = transaction(database) {
        BusStopsEntity.findById(id)?.delete()
    }

    fun createBusStop(
        busStopsCollectionId: Int,
        latitude: Double,
        longitude: Double,
        address: String,
    ): BusStopEntity = transaction(database) {
        BusStopEntity.new {
            this.busStopsCollection =
                BusStopsEntity(EntityID(busStopsCollectionId, BusStopsTable))
            this.latitude = latitude
            this.longitude = longitude
            this.address = address
        }
    }

    fun createMultipleBusStops(
        busStops: List<BusStop>,
        busStopsId: Int,
    ): List<BusStopEntity> = transaction(database) {
        busStops.map {
            BusStopEntity.new {
                this.latitude = it.lat
                this.longitude = it.lon
                this.busStopsCollection = BusStopsEntity(EntityID(busStopsId, BusStopsTable))
                this.address = it.address
            }
        }
    }

    fun getBusStopById(id: Int): BusStopEntity? = transaction(database) { BusStopEntity.findById(id) }

    fun getAllBusStops(): List<BusStopEntity> = transaction(database) { BusStopEntity.all().toList() }

    fun updateBusStop(id: Int, latitude: Double, longitude: Double, address: String) = transaction(database) {
        val busStop = BusStopEntity.findById(id)
        busStop?.latitude = latitude
        busStop?.longitude = longitude
        busStop?.address = address
    }

    fun deleteBusStop(id: Int) = transaction(database) { BusStopEntity.findById(id)?.delete() }

    fun createFitnessList(busStopsCollectionId: Int): FitnessListEntity = transaction(database) {
        FitnessListEntity.new {
            this.busStops = BusStopsEntity(EntityID(busStopsCollectionId, BusStopsTable))
        }
    }

    fun getFitnessListById(id: Int): FitnessListEntity? = transaction(database) {
        FitnessListEntity.findById(id)
    }

    fun getAllFitnessList(): List<FitnessListEntity> = transaction(database) {
        FitnessListEntity.all().toList()
    }

    fun updateFitnessList(id: Int, busStopsId: Int) = transaction(database) {
        val fitnessListEntity = FitnessListEntity.findById(id)
        fitnessListEntity?.busStops =
            BusStopsEntity(EntityID(busStopsId, BusStopsTable))
    }

    fun deleteFitnessList(id: Int) = transaction(database) { FitnessListEntity.findById(id)?.delete() }

    fun createFitness(fitnessListId: Int, maxFitness: Double, avgFitness: Double): FitnessEntity = transaction(database) {
        FitnessEntity.new {
            this.fitnessListId = FitnessListEntity(EntityID(fitnessListId, FitnessListTable))
            this.maxFitness = maxFitness
            this.avgFitness = avgFitness
        }

    }

    fun getFitnessById(id: Int): FitnessEntity? = transaction(database) { FitnessEntity.findById(id) }

    fun getAllFitness(): List<FitnessEntity> = transaction(database) { FitnessEntity.all().toList() }

    fun updateFitness(id: Int, maxFitness: Double, avgFitness: Double) = transaction(database) {
        val fitness = FitnessEntity.findById(id)
        fitness?.maxFitness = maxFitness
        fitness?.avgFitness = avgFitness
    }

    fun deleteFitness(id: Int) = transaction(database) { FitnessEntity.findById(id)?.delete() }

    fun createRouteList(busStopsId: Int): RouteListEntity = transaction(database) {
        RouteListEntity.new {
            this.busStops = BusStopsEntity(EntityID(busStopsId, BusStopsTable))
        }
    }

    fun getRouteListById(id: Int): RouteListEntity? = transaction(database) { RouteListEntity.findById(id) }

    fun getAllRouteLists(): List<RouteListEntity> = transaction(database) { RouteListEntity.all().toList() }

    fun updateRouteList(id: Int, busStopsId: Int) = transaction(database) {
        val routesList = RouteListEntity.findById(id)
        routesList?.busStops = BusStopsEntity(EntityID(busStopsId, BusStopsTable))
    }

    fun deleteRouteList(id: Int) = transaction(database) {
        RouteListEntity.findById(id)?.delete()
    }

    // CRUD methods for Routes...
    fun createRoute(routesListId: Int, busStopId: Int) = transaction(database) {
        RouteEntity.new {
            this.routes = RouteListEntity(EntityID(routesListId, RouteListTable))
            this.busStop = BusStopEntity(EntityID(busStopId, BusStopTable))
        }
    }

    fun createMultipleRoutes(routesListId: Int, busStopIds: List<Int>) = transaction(database) {
        busStopIds.map { createRoute(routesListId, it) }
    }


    fun getRouteById(id: Int): RouteEntity? = transaction(database) { RouteEntity.findById(id) }

    fun getAllRoutes(): List<RouteEntity> = transaction(database) { RouteEntity.all().toList() }

    fun updateRoute(id: Int, busStopId: Int) = transaction(database) {
        val route = RouteEntity.findById(id)
        route?.busStop = BusStopEntity(EntityID(busStopId, BusStopTable))
    }

    fun deleteRoute(id: Int) = transaction(database) {
        RouteEntity.findById(id)?.delete()
    }
}

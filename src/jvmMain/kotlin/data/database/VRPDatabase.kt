package data.database

import center.sciprog.maps.coordinates.Gmc
import center.sciprog.maps.features.Rectangle
import data.database.entities.*
import data.database.entities.BusStopTable
import domain.poko.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

object VRPDatabase {
    private val database: Database = Database.connect(
        url = "jdbc:postgresql://localhost/vrp_database",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "qwerty"
    )

    init {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        setup()
    }

    private fun setup() {
        transaction(database) {
            addLogger(StdOutSqlLogger)
            if (!BusStopsTable.exists()) SchemaUtils.create(BusStopsTable)
            if (!BusStopTable.exists()) SchemaUtils.create(BusStopTable)
            if (!FitnessListTable.exists()) SchemaUtils.create(FitnessListTable)
            if (!FitnessTable.exists()) SchemaUtils.create(FitnessTable)
            if (!RouteListTable.exists()) SchemaUtils.create(RouteListTable)
            if (!RouteTable.exists()) SchemaUtils.create(RouteTable)
        }
    }

    fun createBusStops(searchBox: Rectangle<Gmc>): BusStops = transaction(database) {
        addLogger(StdOutSqlLogger)
        val busStopEntity = BusStopsEntity.find {
            (BusStopsTable.startSearchBoxLatitude eq searchBox.a.latitude.toDegrees().value) and
                    (BusStopsTable.startSearchBoxLongitude eq searchBox.a.longitude.toDegrees().value) and
                    (BusStopsTable.endSearchBoxLatitude eq searchBox.b.latitude.toDegrees().value) and
                    (BusStopsTable.endSearchBoxLongitude eq searchBox.b.longitude.toDegrees().value)
        }.firstOrNull()
        if (busStopEntity != null) return@transaction busStopEntity.toBusStops()
        BusStopsEntity.new {
            this.startSearchBoxLatitude = searchBox.a.latitude.toDegrees().value
            this.startSearchBoxLongitude = searchBox.a.longitude.toDegrees().value
            this.endSearchBoxLatitude = searchBox.b.latitude.toDegrees().value
            this.endSearchBoxLongitude = searchBox.b.longitude.toDegrees().value
        }.toBusStops()
    }

    fun getBusStopsById(id: Int): BusStops? =
        transaction(database) {
            addLogger(StdOutSqlLogger)
            BusStopsEntity.findById(id)?.toBusStops()
        }

    fun getAllBusStopCollections(): List<BusStops> =
        transaction(database) {
            addLogger(StdOutSqlLogger)
            BusStopsEntity.all().map { it.toBusStops() }
        }

    fun deleteBusStops(id: Int) = transaction(database) {
        addLogger(StdOutSqlLogger)
        BusStopsEntity.findById(id)?.delete()
    }

    fun createBusStop(
        id: Int,
        busStops: BusStops,
        latitude: Double,
        longitude: Double,
        address: String,
    ): BusStop = transaction(database) {
        addLogger(StdOutSqlLogger)
        BusStopEntity.new {
            this.busStops = BusStopsEntity(EntityID(busStops.id!!, BusStopsTable)).apply {
                db = database
                klass = BusStopsEntity
            }
            this.latitude = latitude
            this.longitude = longitude
            this.address = address
            this.index = id
        }.toBusStop()
    }

    fun createMultipleBusStops(
        busStops: List<BusStop>,
    ): List<BusStop> = transaction(database) {
        addLogger(StdOutSqlLogger)
        busStops.map {
            createBusStop(
                busStops = it.busStops,
                latitude = it.lat,
                longitude = it.lon,
                address = it.address,
                id = it.id
            )
        }
    }

    fun getBusStopById(id: Int): BusStop? = transaction(database) {
        addLogger(StdOutSqlLogger)
        BusStopEntity.findById(id)?.toBusStop()
    }

    fun getBusStopsByBusStopsKey(busStops: BusStops): List<BusStop> = transaction(database) {
        addLogger(StdOutSqlLogger)
        BusStopEntity.all().filter { it.busStops.toBusStops() == busStops }.map { it.toBusStop() }
    }

    fun getAllBusStops(): List<BusStop> = transaction(database) {
        addLogger(StdOutSqlLogger)
        BusStopEntity.all().map { it.toBusStop() }
    }

    fun updateBusStop(id: Int, latitude: Double, longitude: Double, address: String) = transaction(database) {
        addLogger(StdOutSqlLogger)
        val busStop = BusStopEntity.findById(id)
        busStop?.latitude = latitude
        busStop?.longitude = longitude
        busStop?.address = address
    }

    fun deleteBusStop(id: Int) = transaction(database) {
        addLogger(StdOutSqlLogger)
        BusStopEntity.findById(id)?.delete()
    }

    fun createFitnessList(busStops: BusStops): FitnessList = transaction(database) {
        addLogger(StdOutSqlLogger)
        FitnessListEntity.new {
            this.busStops = BusStopsEntity(EntityID(busStops.id!!, BusStopsTable))
        }.toFitnessList()
    }

    fun getFitnessListById(id: Int): FitnessList? = transaction(database) {
        addLogger(StdOutSqlLogger)
        FitnessListEntity.findById(id)?.toFitnessList()
    }

    fun getAllFitnessList(): List<FitnessList> = transaction(database) {
        addLogger(StdOutSqlLogger)
        FitnessListEntity.all().map { it.toFitnessList() }
    }

    fun updateFitnessList(id: Int, busStops: BusStops) = transaction(database) {
        addLogger(StdOutSqlLogger)
        val fitnessListEntity = FitnessListEntity.findById(id)
        fitnessListEntity?.busStops = BusStopsEntity(EntityID(busStops.id, BusStopsTable))
    }

    fun deleteFitnessList(id: Int) = transaction(database) {
        addLogger(StdOutSqlLogger)
        FitnessListEntity.findById(id)?.delete()
    }

    fun createFitness(fitnessList: FitnessList, maxFitness: Double, avgFitness: Double): Fitness =
        transaction(database) {
            addLogger(StdOutSqlLogger)
            FitnessEntity.new {
                this.fitnessList = FitnessListEntity(fitnessList.id)
                this.maxFitness = maxFitness
                this.avgFitness = avgFitness
            }.toFitness()
        }

    fun getFitnessById(id: Int): Fitness? = transaction(database) {
        addLogger(StdOutSqlLogger)
        FitnessEntity.findById(id)?.toFitness()
    }

    fun getAllFitness(): List<Fitness> = transaction(database) {
        addLogger(StdOutSqlLogger)
        FitnessEntity.all().map { it.toFitness() }
    }

    fun updateFitness(id: Int, maxFitness: Double, avgFitness: Double) = transaction(database) {
        addLogger(StdOutSqlLogger)
        val fitness = FitnessEntity.findById(id)
        fitness?.maxFitness = maxFitness
        fitness?.avgFitness = avgFitness
    }

    fun deleteFitness(id: Int) = transaction(database) {
        addLogger(StdOutSqlLogger)
        FitnessEntity.findById(id)?.delete()
    }

    fun createRouteList(busStops: BusStops, type: String): RouteList = transaction(database) {
        addLogger(StdOutSqlLogger)
        RouteListEntity.new {
            this.busStops = BusStopsEntity(EntityID(busStops.id, BusStopsTable)).apply {
                db = database
                klass = BusStopsEntity
            }
            this.type = type
        }.toRouteList()
    }

    fun getRouteListById(id: Int): RouteList? = transaction(database) {
        addLogger(StdOutSqlLogger)
        RouteListEntity.findById(id)?.toRouteList()
    }

    fun getAllRouteLists(): List<RouteList> = transaction(database) {
        addLogger(StdOutSqlLogger)
        RouteListEntity.all().map { it.toRouteList() }
    }

    fun updateRouteList(id: Int, busStops: BusStops) = transaction(database) {
        addLogger(StdOutSqlLogger)
        val routesList = RouteListEntity.findById(id)
        routesList?.busStops = BusStopsEntity(EntityID(busStops.id, BusStopsTable))
    }

    fun deleteRouteList(id: Int) = transaction(database) {
        addLogger(StdOutSqlLogger)
        RouteListEntity.findById(id)?.delete()
    }

    // CRUD methods for Routes...
    fun createRoute(routesList: RouteList, busStop: BusStop): Route = transaction(database) {
        addLogger(StdOutSqlLogger)
        RouteEntity.new {
            this.routes = RouteListEntity(routesList.id).apply {
                db = database
                klass = RouteListEntity
            }
            this.busStop = BusStopEntity(EntityID(busStop.id, BusStopTable)).apply {
                db = database
                klass = BusStopEntity
            }

        }.toRoute()
    }

    fun createMultipleRoutes(routesList: RouteList, busStops: List<BusStop>): List<Route> =
        transaction(database) {
            addLogger(StdOutSqlLogger)
            busStops.map { createRoute(routesList, it) }
        }


    fun getRouteById(id: Int): Route? = transaction(database) {
        addLogger(StdOutSqlLogger)
        RouteEntity.findById(id)?.toRoute()
    }

    fun getAllRoutes(): List<Route> = transaction(database) {
        addLogger(StdOutSqlLogger)
        RouteEntity.all().map { it.toRoute() }
    }

    fun updateRoute(id: Int, busStopId: Int) = transaction(database) {
        addLogger(StdOutSqlLogger)
        val route = RouteEntity.findById(id)
        route?.busStop = BusStopEntity(EntityID(busStopId, BusStopTable))
    }

    fun deleteRoute(id: Int) = transaction(database) {
        addLogger(StdOutSqlLogger)
        RouteEntity.findById(id)?.delete()
    }

    fun getBusStopsBySearchBox(searchBox: Rectangle<Gmc>): BusStops? = transaction(database) {
        addLogger(StdOutSqlLogger)
        BusStopsEntity.find {
            (BusStopsTable.startSearchBoxLatitude eq searchBox.a.latitude.toDegrees().value) and
                    (BusStopsTable.startSearchBoxLongitude eq searchBox.a.longitude.toDegrees().value) and
                    (BusStopsTable.endSearchBoxLatitude eq searchBox.b.latitude.toDegrees().value) and
                    (BusStopsTable.endSearchBoxLongitude eq searchBox.b.longitude.toDegrees().value)
        }.firstOrNull()?.toBusStops()
    }
}

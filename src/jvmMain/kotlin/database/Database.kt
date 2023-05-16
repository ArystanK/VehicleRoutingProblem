package database

import BusStop
import BusStopTable
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Database {
    private fun connectToDatabase() {
        org.jetbrains.exposed.sql.Database.connect(
            url = "jdbc:postgresql://localhost:5432/vehicle_routing_problem",
            password = "qwerty",
            user = "postgres",
            driver = "org.postgresql.Driver"
        )
    }

    private fun initializeDatabase() {
        transaction {
            addLogger(StdOutSqlLogger)
        }
    }

    init {
        connectToDatabase()
        initializeDatabase()
    }

    fun saveData(busStops: List<BusStop>) {
        transaction {
            addLogger(StdOutSqlLogger)
            BusStopTable.batchInsert(busStops) {
                this[BusStopTable.id] = it.id
                this[BusStopTable.lat] = it.lat
                this[BusStopTable.lon] = it.lon
                this[BusStopTable.address] = it.address
            }
        }
    }

    fun getData(): List<BusStop> {
        return transaction {
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
    }
}
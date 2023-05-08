
import utility.Util
import utility.ZLog
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import java.util.logging.Logger

fun main(args: Array<String>) {
    val logger = ZLog::class.java.let { Logger.getLogger(it.name) }

    val prop = Properties()
    try {
        val input = FileInputStream("test.properties") // TODO: rename to 'config.properties'
        prop.load(input)
        input.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    val sqlitePath = try {
        prop.getProperty("phantombotDB.path")
    } catch (e: Exception) {
        logger.warning("Filename 'test.properties' @ property 'phantombotDB.path': $e \nDefaulting to 'phantombot.db'\nThis will target the SQLite database if it is in the same directory as this application.")
        "phantombot.db"
    }

    val syncInterval = try {
        prop.getProperty("syncInterval").toInt()
    } catch (e: Exception) {
        logger.warning("Filename 'test.properties' @ property 'syncInterval': Must be an integer. Defaulting to 15 minute sync interval.")
        15 // default to 15 min interval
    }

    logger.info(">sqlite path: $sqlitePath")
    logger.info(">sync interval: $syncInterval")

    // Set up timer to execute the retrieval and insertion at user-set intervals
    val timer = Timer()

    val query = "SELECT variable, value FROM phantombot_command"
    val mongoConnStr = "mongodb+srv://jasonj:7toDQJD96ZECE5xd@zsrcluster.uq3lwwn.mongodb.net/?retryWrites=true&w=majority"

    val intervalMillis: Long = Util.minutesToMS(syncInterval)
    timer.scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
            // open SQLite
            val sqliteWatcher = SQLiteWatcher(sqlitePath)

            val sqliteData = sqliteWatcher.readFromTable(query)
            // close SQLite
            sqliteWatcher.closeConnection()

            // Connect to MongoDB Atlas
            val mongoConn = MongoConnect(mongoConnStr, "dbName", "collection1")

            val mongoData = mongoConn.getData()

            logger.info(">sqlite data: $sqliteData")
            logger.info(">mongo data: $mongoData")

            // find key-value pairs present in SQLite but not uploaded to MongoDB
            val sqlDataKeys = sqliteData.map { it.first }
//            val sqlDataValues = sqliteData.map { it.second }
            val mongoDataKeys = mongoData.map { it.first }
            val mongoDataValues = mongoData.map { it.second }

            // sets /////////
            // only in SQLite -> create
            val onlyInSQLite = sqliteData.filter { it.first !in mongoDataKeys }
            logger.info(">sqlite only: $onlyInSQLite")

            // only in Mongo -> delete
            val onlyInMongo = mongoData.filter { it.first !in sqlDataKeys }
            logger.info(">mongo only: $onlyInMongo")

            // in SQLite and Mongo but have different values -> update
            val matchingKeys = sqliteData.filter { it.first in mongoDataKeys && it.second !in mongoDataValues }
            logger.info(">shared key different value: $matchingKeys")


            // CREATE
            if (onlyInSQLite.isNotEmpty()) {
                logger.info(">>adding $onlyInSQLite to mongoDB")
                mongoConn.createData(onlyInSQLite)
                logger.info(">>added ${onlyInSQLite.size} commands $onlyInSQLite to mongoDB")
            }

            // UPDATE
            if (matchingKeys.isNotEmpty()) {
                logger.info(">>updating $matchingKeys in mongoDB")
                mongoConn.updateData(matchingKeys)
                logger.info(">>updated $matchingKeys in mongoDB")
            }

            // DELETE
            if (onlyInMongo.isNotEmpty()) {
                logger.info(">>removing $onlyInMongo from mongoDB")
                mongoConn.deleteData(onlyInMongo)
                logger.info(">>removed $onlyInMongo from mongoDB")
            }


            // close mongo
            logger.info(">final mongo data: ${mongoConn.getData()}")
            mongoConn.closeConnection()

        }
    }, 0, intervalMillis)

}

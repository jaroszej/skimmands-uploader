
import utility.Util
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.FileHandler
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

fun main() {

    try {
        // Create log directory
        val logDir = File("").absolutePath + File.separator + "skimmands_uploader_logs"

        val logDirFile = File(logDir)
        if (!logDirFile.exists()) {
            logDirFile.mkdirs()
        }

        // Set up logger
        val logger = Logger.getLogger("skimmands")

        logger.info("Initializing...")
        logger.useParentHandlers = false

        // Create file handler to write to log files
        val maxLogSize = 1024 * 1024 // 1024 MB
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
        val date = dateFormat.format(System.currentTimeMillis())
        val fileHandler = FileHandler("$logDir/skimmands_log_$date.log", maxLogSize, 5, true)
        fileHandler.formatter = SimpleFormatter()


        // Add file handler to logger
        logger.addHandler(fileHandler)

        // Log startup message
        logger.info("Starting the Skimmands Uploader!")

        val syncInterval = System.getProperty("syncInterval")?.toInt() ?: 15
        val sqlitePath = System.getProperty("sqlitePath") ?: "config/phantombot.db"
        val mongoConnStr = System.getProperty("mongoConnStr") ?: "mongodb://localhost:27017"

        logger.info("### CONFIGURATION PARAMETERS: ###")
        logger.info("sync interval: $syncInterval")
        logger.info("sqlite path: $sqlitePath")
        logger.info("mongodb uri: $mongoConnStr")
        logger.info("#################################")

        // Set up timer to execute the retrieval and insertion at user-set intervals
        val timer = Timer()

        // Query to grab data from SQLite
        val query = "SELECT variable, value FROM phantombot_command"

        // convert minutes to MS for timer
        val intervalMillis: Long = Util.minutesToMS(syncInterval)
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // open SQLite
                val sqliteWatcher = SQLiteWatcher(sqlitePath)

                val sqliteData = sqliteWatcher.readFromTable(query)
                // close SQLite
                sqliteWatcher.closeConnection()

                // Connect to MongoDB Atlas
                val mongoConn = MongoConnect(mongoConnStr.toString(), "skimmands", "commands")

                val mongoData = mongoConn.getData()

                // find key-value pairs present in SQLite but not uploaded to MongoDB
                val sqlDataKeys = sqliteData.map { it.first }
    //            val sqlDataValues = sqliteData.map { it.second }
                val mongoDataKeys = mongoData.map { it.first }
                val mongoDataValues = mongoData.map { it.second }

                // sets /////////
                // only in SQLite -> create
                val onlyInSQLite = sqliteData.filter { it.first !in mongoDataKeys }
                logger.info("> sqlite only: $onlyInSQLite")

                // only in Mongo -> delete
                val onlyInMongo = mongoData.filter { it.first !in sqlDataKeys }
                logger.info("> mongo only: $onlyInMongo")

                // in SQLite and Mongo but have different values -> update
                val matchingKeys = sqliteData.filter { it.first in mongoDataKeys && it.second !in mongoDataValues }
                logger.info("> shared key different value: $matchingKeys")


                // CREATE
                if (onlyInSQLite.isNotEmpty()) {
                    logger.info(">> adding $onlyInSQLite to mongoDB")
                    val createSuccess = mongoConn.createData(onlyInSQLite)
                    if (createSuccess) {
                        logger.info(">> added ${onlyInSQLite.size} commands $onlyInSQLite to mongoDB")
                    } else {
                        logger.severe(">> Error creating data in MongoDB")
                    }
                }

                // UPDATE
                if (matchingKeys.isNotEmpty()) {
                    logger.info(">> updating $matchingKeys in mongoDB")
                    val updateSuccess = mongoConn.updateData(matchingKeys)
                    if (updateSuccess) {
                        logger.info(">> updated $matchingKeys in mongoDB")
                    } else {
                        logger.severe(">> Error updating data in MongoDB")
                    }
                }

                // DELETE
                if (onlyInMongo.isNotEmpty()) {
                    logger.info(">> removing $onlyInMongo from mongoDB")
                    val success = mongoConn.deleteData(onlyInMongo)
                    if (success) {
                        logger.info(">> removed $onlyInMongo from mongoDB")
                    } else {
                        logger.severe(">> failed to remove $onlyInMongo from mongoDB")
                    }
                }


                // close mongo
                mongoConn.closeConnection()

            }
        }, 0, intervalMillis)

    } catch (e: IOException) {
        println("Error creating log directory or file: ${e.message}")
    }
}

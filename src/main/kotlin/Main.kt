
import java.util.logging.Logger

fun main(args: Array<String>) {
//    val mainGUI = MainGUI("Skimmands Uploader")

    val logger = ZLog::class.java.let { Logger.getLogger(it.name) }

    val dbPath = "R:/Documents/skis phantombot/3.6.5.2/PhantomBot-3.6.5.2/config/phantombot.db"
    // open SQLite
    val watcher = SQLiteWatcher(dbPath)

    val query = "SELECT variable, value FROM phantombot_command"
    val localCommands = watcher.readFromTable(query)
    // close SQLite
    watcher.closeConnection()
    val sqlFinish = Thread.currentThread().stackTrace[1].lineNumber

    // Connect to MongoDB Atlas
    val mongoConnStr = "mongodb+srv://jasonj:7toDQJD96ZECE5xd@zsrcluster.uq3lwwn.mongodb.net/?retryWrites=true&w=majority"
    val mongoConn = MongoConnect(mongoConnStr, "dbName", "collection1")

    val data = mongoConn.getData()
    mongoConn.closeConnection()
    logger.info("LnNo:$sqlFinish -> local phantombot commands: $localCommands")
    logger.info("LnNo:${Thread.currentThread().stackTrace[1].lineNumber} -> mongo data: $data")

    // find key-value pairs present in SQLite but not uploaded to MongoDB
    val sqlDataKeys = localCommands.map { it.first }
    val mongoDataKeys = data.map { it.first }
    val onlyInSQLite = localCommands.filter { it.first !in mongoDataKeys }
    val result = onlyInSQLite.map { Pair(it.first, it.second) }
    logger.info("LnNo:${Thread.currentThread().stackTrace[1].lineNumber} -> SQLite data not in mongoDB: $result")

//
//    // Set up timer to execute the retrieval and insertion at user-set intervals
//    val timer = Timer()
//    timer.scheduleAtFixedRate(object : TimerTask() {
//        override fun run() {
//            try {
//                // Retrieve data from SQLite database
//                val resultSet = sqliteStmt.executeQuery("SELECT * FROM myTable")
//                val gson = Gson()
//                val jsonArray = ArrayList<JsonObject>()
//                while (resultSet.next()) {
//                    val jsonObject = JsonObject()
//                    jsonObject.addProperty("id", resultSet.getInt("id"))
//                    jsonObject.addProperty("name", resultSet.getString("name"))
//                    jsonArray.add(jsonObject)
//                }
//
//                // Insert data into MongoDB Atlas database
//                val documents = ArrayList<Document>()
//                for (jsonObject in jsonArray) {
//                    val document = Document.parse(gson.toJson(jsonObject))
//                    documents.add(document)
//                }
//                collection.insertMany(documents)
//            } catch (e: Exception) {
//                Logger.getLogger("MyApp").warning("Error while executing data retrieval and insertion: ${e.message}")
//            }
//        }
//    }, 0, intervalMillis)

}

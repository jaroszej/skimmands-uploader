
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Projections
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import java.util.logging.Level
import java.util.logging.Logger

class MongoConnect(uri: String, databaseName: String, collectionName: String) {

    private val collection: MongoCollection<Document>
    private val mongoClient = MongoClients.create(uri)
    private val logger = Logger.getLogger(MongoConnect::class.java.name)

    init {
        // Get a reference to the specified database and collection
        val database = mongoClient.getDatabase(databaseName)
        collection = database.getCollection(collectionName)
        logger.level = Level.INFO
    }

    fun getCollection(): MongoCollection<Document> {
        return collection
    }

    fun getData(): MutableList<Pair<String, String>> {
        val projection = Projections.include("variable", "value")
        val data = mutableListOf<Pair<String, String>>()

        collection.find().projection(projection).forEach {
            val variable = it["variable"].toString()
            val value = it["value"].toString()
            data.add(Pair(variable, value))
        }

        return data
    }

    fun createData(data: List<Pair<String, String>>): Boolean {
        return try {
            if (data.isEmpty()) {
                // Return true immediately if data is empty
                return true
            }
            val documents = data.map { Document("variable", it.first).append("value", it.second) }
            collection.insertMany(documents)
            true
        } catch (e: Exception) {
            logger.warning("Error creating data in MongoDB: $e}")
            false
        }
    }

    fun updateData(data: List<Pair<String, String>>): Boolean {
        return try {
            data.forEach {
                val variable = it.first
                val value = it.second
                collection.updateOne(eq("variable", variable), Document("\$set", Document("value", value)), UpdateOptions().upsert(true))
            }
            true
        } catch (e: Exception) {
            logger.warning("Error updating data in MongoDB: $e")
            false
        }
    }

    fun deleteData(data: List<Pair<String, String>>): Boolean {
        return try {
            val variables = data.map { it.first }
            collection.deleteMany(eq("variable", Document("\$nin", variables)))
            true
        } catch (e: Exception) {
            logger.warning("Error deleting data in MongoDB: $e")
            false
        }
    }

    fun closeConnection() {
        try {
            // Close the connection to MongoDB
            mongoClient.close()
        } catch (e: Exception) {
            logger.warning("Error closing connection to MongoDB: $e")
        }
    }
}

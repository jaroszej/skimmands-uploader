import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.logging.Logger

class SQLiteWatcher(private val path: String) {
    private var connection: Connection? = null
    private val logger: Logger = Logger.getLogger(SQLiteWatcher::class.java.name)

    init {
        try {
            Class.forName("org.sqlite.JDBC")
            connection = DriverManager.getConnection("jdbc:sqlite:$path")
        } catch (e: ClassNotFoundException) {
            logger.severe("Failed to load SQLite JDBC driver: $e")
            closeConnection()
        } catch (e: SQLException) {
            logger.severe("Failed to establish database connection: $e")
            closeConnection()
        }
    }

    fun readFromTable(query: String): List<Pair<String, String>> {
        val statement = connection?.createStatement()
        return try {
            val resultSet = statement?.executeQuery(query)
            val items = mutableListOf<Pair<String, String>>()
            while (resultSet?.next() == true) {
                val variable = resultSet.getString("variable")
                val value = resultSet.getString("value")
                items.add(Pair(variable, value))
            }
            items
        } catch (e: SQLException) {
            logger.severe("Error executing SQL query on database at $path: $e")
            emptyList()
        } catch (e: ClassNotFoundException) {
            logger.severe("Failed to load SQLite JDBC driver: $e")
            emptyList()
        } catch (e: IllegalStateException) {
            logger.severe("Failed to establish database connection at $path: $e")
            emptyList()
        } finally {
            statement?.close()
        }
    }

    fun closeConnection() {
        connection?.let {
            try {
                it.close()
            } catch (e: SQLException) {
                logger.severe("Error closing SQLite connection: $e")
            }
            connection = null
        }
    }
}

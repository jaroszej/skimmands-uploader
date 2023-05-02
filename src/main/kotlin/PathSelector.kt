
import org.sqlite.SQLiteException
import java.awt.Component
import java.io.File
import java.sql.DriverManager
import java.sql.SQLException
import java.util.logging.Logger
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

class PathSelector {

    fun chooseFile(parentComponent: Component): File? {
        val logger = ZLog::class.java.let { Logger.getLogger(it.name) }

        val chooser = JFileChooser()
        chooser.dialogTitle = "Choose a .db file"
        chooser.fileFilter = FileNameExtensionFilter("Database files", "db")
        chooser.fileSelectionMode = JFileChooser.FILES_ONLY

        val result = chooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            val file = chooser.selectedFile
            try {
                val connection = DriverManager.getConnection("jdbc:sqlite:${file.absolutePath}")

                connection.close()

                logger.info("LnNo:${Thread.currentThread().stackTrace[1].lineNumber}-> phantombot.db path: ${file.absolutePath}")
                return file
            } catch (e: SQLiteException) {
                // file is not an SQLite database
                JOptionPane.showMessageDialog(parentComponent, "The selected file is not a valid SQLite database.", "Error", JOptionPane.ERROR_MESSAGE)
                e.printStackTrace()
            } catch (e: SQLException) {
                // file is not an SQLite database
                JOptionPane.showMessageDialog(parentComponent, "The selected file is not a valid SQLite database.", "Error", JOptionPane.ERROR_MESSAGE)
                e.printStackTrace()
            }
        }

        return null
    }
}

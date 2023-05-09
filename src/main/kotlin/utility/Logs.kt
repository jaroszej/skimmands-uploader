package utility

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.*
import java.util.logging.Formatter

class ZLog(className: Class<*>) {

    companion object {
        private val MAX_LOG_FILES = 5
        private val MAX_LOG_FILE_SIZE_BYTES = 1024 * 1024
        private val LOG_DIRECTORY_NAME = "skimmands_uploader_logs"
        private val LOG_FILE_PREFIX = "skimmands-"
        private val LOG_FILE_EXTENSION = ".log"
        private val LOGGER = Logger.getLogger(ZLog::class.java.name)
        private val LOG_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
    }

    init {
        val consoleHandler = ConsoleHandler()
        consoleHandler.formatter = CustomFormatter(className)
        LOGGER.addHandler(consoleHandler)

        try {
            val logDirectory = File(LOG_DIRECTORY_NAME)
            if (!logDirectory.exists()) {
                logDirectory.mkdirs()
            }

            val logFiles = getLogFilesSortedByDate(logDirectory)
            if (logFiles.size >= MAX_LOG_FILES) {
                logFiles.last().delete()
            }

            val logFilePath = "${logDirectory.absolutePath}/$LOG_FILE_PREFIX${LOG_DATE_FORMAT.format(Date())}$LOG_FILE_EXTENSION"
            val fileHandler = FileHandler(logFilePath, MAX_LOG_FILE_SIZE_BYTES, 1, true)
            fileHandler.formatter = CustomFormatter(className)
            LOGGER.addHandler(fileHandler)

        } catch (e: IOException) {
            LOGGER.warning("Failed to initialize file handler: ${e.message}")
        }

        LOGGER.level = Level.ALL
    }

    private fun getLogFilesSortedByDate(directory: File): List<File> {
        return directory.listFiles { _, name ->
            name.startsWith(LOG_FILE_PREFIX) && name.endsWith(LOG_FILE_EXTENSION)
        }?.sortedByDescending { file -> file.lastModified() } ?: emptyList()
    }

    class CustomFormatter(private val className: Class<*>) : Formatter() {
        override fun format(record: LogRecord): String {
            val sourceClassName = record.sourceClassName ?: className.name
            return "[${sourceClassName}] ${record.message}\n"
        }
    }
}

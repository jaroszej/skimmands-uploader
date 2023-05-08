package utility

import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.*
import java.util.logging.Formatter


class ZLog(className: Class<*>) {
    init {
        val consoleHandler: Handler = ConsoleHandler()
        consoleHandler.formatter = CustomFormatter(className)
        LOGGER.addHandler(consoleHandler)
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")

        val fileHandler: Handler
        try {
            fileHandler = FileHandler("skimmands-${sdf.format(Date())}.log", 1024 * 1024, 5, true)
            fileHandler.formatter = CustomFormatter(className)
            LOGGER.addHandler(fileHandler)
        } catch (e: IOException) {
            LOGGER.warning("Failed to initialize file handler: ${e.message}")
        }

        LOGGER.level = Level.ALL
    }

    class CustomFormatter(private val className: Class<*>) : Formatter() {
        override fun format(record: LogRecord): String {
            val sourceClassName = record.sourceClassName ?: className.name
            return "[${sourceClassName}] ${record.message}\n"
        }
    }

    companion object {
        private val LOGGER = Logger.getLogger(ZLog::class.java.name)
    }
}

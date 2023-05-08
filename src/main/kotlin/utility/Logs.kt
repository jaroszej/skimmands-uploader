package utility

import java.util.logging.*


class ZLog(className: Class<*>) {
    init {
        val consoleHandler: Handler = ConsoleHandler()
        consoleHandler.formatter = CustomFormatter(className)
        LOGGER.addHandler(consoleHandler)
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

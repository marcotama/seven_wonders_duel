package com.aigamelabs.utils

import java.io.*
import java.util.logging.Formatter
import java.util.logging.LogRecord

/**
 * Print a brief summary of the LogRecord in a human readable
 * format.  The summary will typically be 1 or 2 lines.
 *
 * @since 1.4
 */

class MinimalFormatter : Formatter() {

    private val lineSeparator = "\n"

    /**
     * Format the given LogRecord.
     *
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    @Synchronized
    override fun format(record: LogRecord): String {
        val sb = StringBuffer()
        val message = formatMessage(record)
        sb.append(message)
        sb.append(lineSeparator)
        if (record.thrown != null) {
            try {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                record.thrown.printStackTrace(pw)
                pw.close()
                sb.append(sw.toString())
            } catch (ex: Exception) {
            }
        }
        return sb.toString()
    }
}

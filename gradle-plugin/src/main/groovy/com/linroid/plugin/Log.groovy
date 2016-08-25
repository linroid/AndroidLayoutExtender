package com.linroid.plugin

import org.gradle.api.logging.Logger

/**
 * @author linroid <linroid@gmail.com>
 * @since 8/24/16
 */
class Log {
    private static Logger sLogger;

    static void setupLogger(Logger sLogger) {
        Log.sLogger = sLogger
    }

    static void d(String message, Object... args) {
        String log = formatMessage(message, args);
        sLogger.debug(log)
    }

    static void i(String message, Object... args) {
        String log = formatMessage(message, args);
        sLogger.info(log)
    }


    static void e(Throwable error, String message, Object... args) {
        String log = formatMessage(message, args);
        sLogger.error(log, error)
    }

    static void q(String message, Object... args) {
        String log = formatMessage(message, args);
        sLogger.quiet(log)
    }

    private static String formatMessage(String message, Object... args) {
        return "[AndroidXmlExtender]" + String.format(message, args);
    }
}

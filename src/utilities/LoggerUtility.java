package utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * LoggerUtility.java
 * This utility class provides methods for logging error and info messages to separate log files.
 * It creates a directory for logs if it doesn't exist and handles the creation of log files.
 * It also formats the log messages with timestamps and stack traces for errors.
 */

public class LoggerUtility {
    private static final String LOG_DIR = "logs";
    private static final String ERROR_LOG = "logs/error.log";
    private static final String INFO_LOG = "logs/info.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        try {
            File directory = new File(LOG_DIR);
            if (!directory.exists()) {
                directory.mkdir();
            }
            new File(ERROR_LOG).createNewFile();
            new File(INFO_LOG).createNewFile();
        } catch (IOException e) {
            System.err.println("Failed to initialize logging system: " + e.getMessage());
        }
    }
    /**
     * Logs an error message along with the stack trace of the exception to the error log file.
     * @param message The error message to log.
     * @param e The exception whose stack trace will be logged.
     */

    public static void logError(String message, Exception e) {
        String logEntry = String.format("[%s] ERROR: %s - %s%nStacktrace: %s%n", 
            DATE_FORMAT.format(new Date()), 
            message,
            e.getMessage(),
            getStackTraceAsString(e)
        );
        writeToLog(ERROR_LOG, logEntry);
    }

    /**
     * Logs an informational message to the info log file.
     * @param message The informational message to log.
     */
    public static void logInfo(String message) {
        String logEntry = String.format("[%s] INFO: %s%n", 
            DATE_FORMAT.format(new Date()), 
            message
        );
        writeToLog(INFO_LOG, logEntry);
    }

    /**
     * Logs a warning message to the info log file.
     * @param message The warning message to log.
     */

    private static String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("\t").append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Writes a log entry to the specified log file.
     * @param logFile The log file to write to.
     * @param message The message to log.
     */
    private static synchronized void writeToLog(String logFile, String message) {
        try {
            Files.write(
                Paths.get(logFile), 
                message.getBytes(), 
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}

package net.webpdf.ant.task.logging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class will format messages in a useful way, that can be logged by a tasks logging methods.
 */
public class AntLogger {

    @NotNull
    private final org.apache.tools.ant.Task task;

    /**
     * An instance of this class will format messages in a useful way, that can be logged by a tasks logging methods.
     *
     * @param task The owning task that shall be managed.
     */
    public AntLogger(@NotNull org.apache.tools.ant.Task task) {
        this.task = task;
    }

    /**
     * Uses the tasks log method to provide a message for logging.
     *
     * @param message  The content of this messages, describing the logged event.
     * @param logLevel The level and therefore context of this logged event.
     * @param logTag   The tag, that has caused the logging of this event.
     */
    private void log(@NotNull String message, @NotNull LogLevel logLevel, @NotNull LogTag logTag) {
        task.log(prependLevel(prependLogTag(appendLocation(message), logTag), logLevel), logLevel.getLevel());
    }

    /**
     * Uses the tasks log method to provide an exception for logging.
     *
     * @param message  The message describing the context in which the exception occurred.
     * @param ex       The Exception, that has occurred and shall be logged.
     * @param logLevel The level and therefore context of this logged event.
     * @param logTag   The tag, that has caused the logging of this event.
     */
    private void log(@Nullable String message, @Nullable Throwable ex, @NotNull LogLevel logLevel, @NotNull LogTag logTag) {
        StringBuilder messageBuilder = new StringBuilder();
        String msg = message;
        if (msg == null || msg.isEmpty()) {
            msg = getExMessage(ex);
        }
        messageBuilder.append(msg);

        if (logLevel.equals(LogLevel.ERROR) && ex != null) {
            messageBuilder.append("\n");
            try (StringWriter writer = new StringWriter(); PrintWriter printer = new PrintWriter(writer)) {
                ex.printStackTrace(printer);
                messageBuilder.append(writer.toString());
            } catch (IOException ignore) {
                //STACKTRACE FAILURE IS VERY UNLIKELY.
            }
        }
        task.log(prependLevel(prependLogTag(appendLocation(messageBuilder.toString()), logTag), logLevel), ex, logLevel.getLevel());
    }

    /**
     * Uses the tasks log method to provide an exception for logging. In this case no message must be provided. The logger
     * shall attempt to find a usable message in the exception structure itself.
     *
     * @param ex       The Exception, that has occurred and shall be logged.
     * @param logLevel The level and therefore context of this logged event.
     * @param logTag   The tag, that has caused the logging of this event.
     */
    private void log(@Nullable Throwable ex, @NotNull LogLevel logLevel, @NotNull LogTag logTag) {
        log(getExMessage(ex), ex, logLevel, logTag);
    }

    /**
     * Returns the most likely exception message.
     *
     * @param ex The exception a message shall be extracted from.
     * @return The most likely exception message.
     */
    @NotNull
    private String getExMessage(@Nullable Throwable ex) {
        if (ex == null) {
            return "";
        }
        String msg = ex.getMessage();
        if ((msg == null || msg.isEmpty())) {
            msg = getExMessage(ex.getCause());
        }

        return msg;
    }

    /**
     * Shall append the location of the currently logged event to the message.
     *
     * @param message The message that shall be modified.
     * @return A message ending with the location of the currently logged event.
     */
    @NotNull
    private String appendLocation(@NotNull String message) {
        return message + "\n    " + LogTag.LOCATION.getTag() + " " + task.getLocation();
    }

    /**
     * Shall prepend the level to the message.
     *
     * @param message  The message, that shall be modified.
     * @param logLevel The level and therefore context of the logged event.
     * @return A message beginning with the currently active log level.
     */
    @NotNull
    private String prependLevel(@NotNull String message, @NotNull LogLevel logLevel) {
        if (logLevel == LogLevel.INFO) {
            return message;
        }
        return logLevel.getMessage() + message;
    }

    /**
     * Prepends the name of the current tag to the message.
     *
     * @param message The message, that shall be modified.
     * @param logTag  The tag that contains the currently logged event.
     * @return A message beginning with currently active tag.
     */
    private String prependLogTag(@NotNull String message, @NotNull LogTag logTag) {
        return logTag.getTag() + " " + message;
    }

    /**
     * Simplified logging method, that will output the given information on log level info.
     *
     * @param message The message that shall be logged.
     * @param logTag  The tag that caused the logged event.
     */
    public void info(@NotNull String message, @NotNull LogTag logTag) {
        log(message, LogLevel.INFO, logTag);
    }

    /**
     * Simplified logging method, that will output the given information on log level info.
     *
     * @param logTag The tag that caused the logged event.
     */
    public void info(@NotNull LogTag logTag) {
        log("", LogLevel.INFO, logTag);
    }

    /**
     * Simplified logging method, that will output the given information on log level error.
     *
     * @param message The message that shall be logged.
     * @param logTag  The tag that caused the logged event.
     */
    public void error(@NotNull String message, @NotNull LogTag logTag) {
        log(message, LogLevel.ERROR, logTag);
    }

    /**
     * Simplified logging method, that will output the given information on log level error.
     *
     * @param message The message that shall be logged.
     * @param ex      The Exception, that has occurred and shall be logged.
     * @param logTag  The tag that caused the logged event.
     */
    public void error(@NotNull String message, @Nullable Throwable ex, @NotNull LogTag logTag) {
        log(message, ex, LogLevel.ERROR, logTag);
    }

    /**
     * Simplified logging method, that will output the given information on log level error.
     *
     * @param ex     The Exception, that has occurred and shall be logged.
     * @param logTag The tag that caused the logged event.
     */
    public void error(@Nullable Throwable ex, @NotNull LogTag logTag) {
        log(ex, LogLevel.ERROR, logTag);
    }

    /**
     * Simplified logging method, that will output the given information on log level warn.
     *
     * @param message The message that shall be logged.
     * @param logTag  The tag that caused the logged event.
     */
    public void warn(@NotNull String message, @NotNull LogTag logTag) {
        log(message, LogLevel.WARN, logTag);
    }

    /**
     * Simplified logging method, that will output the given information on log level warn.
     *
     * @param message The message that shall be logged.
     * @param ex      The Exception, that has occurred and shall be logged.
     * @param logTag  The tag that caused the logged event.
     */
    public void warn(@NotNull String message, @Nullable Throwable ex, @NotNull LogTag logTag) {
        log(message, ex, LogLevel.WARN, logTag);
    }

    /**
     * Simplified logging method, that will output the given information on log level warn.
     *
     * @param ex     The Exception, that has occurred and shall be logged.
     * @param logTag The tag that caused the logged event.
     */
    public void warn(@Nullable Throwable ex, @NotNull LogTag logTag) {
        log(ex, LogLevel.WARN, logTag);
    }

}

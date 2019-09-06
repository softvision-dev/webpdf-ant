package net.webpdf.ant.task.logging;

import org.apache.tools.ant.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Provides Tags and more complex types for the different numeric Project logging levels.
 */
public enum LogLevel {

    INFO(Project.MSG_INFO, "[INFO] "),
    WARN(Project.MSG_WARN, "[WARN] "),
    ERROR(Project.MSG_ERR, "[ERROR] ");

    @NotNull
    private final String message;

    private final int level;

    /**
     * The level a message shall be logged at.
     *
     * @param level The numeric level the message shall be logged at.
     * @param name  The displayable name of this logging level.
     */
    LogLevel(int level, @NotNull String name) {
        this.level = level;
        this.message = name;
    }

    /**
     * Returns the numeric representation of this LogLevel. This number may be used for filtering log output.
     *
     * @return The numeric representation of this LogLevel.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the printable name of this log level.
     *
     * @return The printable name of this log level.
     */
    @NotNull
    public String getMessage() {
        return message;
    }

}

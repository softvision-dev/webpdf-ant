package net.webpdf.ant.task.logging;

import org.jetbrains.annotations.NotNull;

/**
 * Provides names for the different locations, that may cause the logging of messages.
 */
public enum LogTag {

    LOCATION("[LOCATION]"),
    TARGET("[TARGET]"),
    GROUP("[GROUP]"),
    OPERATION("[OPERATION]"),
    WEBPDF_TASK("[webPDF-TASK]");

    @NotNull
    private final String tag;

    /**
     * A named source of a logged message.
     *
     * @param tag The name of the xml tag, which caused a message to be logged.
     */
    LogTag(@NotNull String tag) {
        this.tag = tag;
    }

    /**
     * Returns the printable name of the source tag of a logged message.
     *
     * @return The printable name of a tag causing the logging of a message.
     */
    @NotNull
    public String getTag() {
        return tag;
    }

}

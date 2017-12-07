package net.webpdf.ant.task.logging;

/**
 * Provides names for the different locations, that may cause the logging of messages.
 */
public enum LogTag {
    LOCATION("[LOCATION]"),
    TARGET("[TARGET]"),
    GROUP("[GROUP]"),
    OPERATION("[OPERATION]"),
    WEBPDF_TASK("[webPDF-TASK]");

    private final String tag;

    /**
     * A named source of a logged message.
     *
     * @param tag The name of the xml tag, which caused a message to be logged.
     */
    LogTag(String tag) {
        this.tag = tag;
    }

    /**
     * Returns the printable name of the source tag of a logged message.
     *
     * @return The printable name of a tag causing the logging of a message.
     */
    public String getTag() {
        return tag;
    }
}

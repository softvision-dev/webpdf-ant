package net.webpdf.ant.task;

import org.jetbrains.annotations.NotNull;

/**
 * Stores names and types for the different defined task classes.
 */
public enum TaskName {

    WEBPDF("webPDF", "webPDF"),
    GROUP("group", "group"),
    OPERATION("operation", "operation"),
    VARIABLE("variable", "variable");

    @NotNull
    private final String name;
    @NotNull
    private final String type;

    /**
     * Stores the given name and type for the enumerated task class.
     *
     * @param name The name of the task.
     * @param type The type of the task.
     */
    TaskName(@NotNull String name, @NotNull String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Return the name of the defined task.
     *
     * @return The name of the defined task.
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Return the type of the defined task.
     *
     * @return The type of the defined task.
     */
    @NotNull
    public String getType() {
        return type;
    }

}

package net.webpdf.ant.task.xml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An instance of this class stores key, original- and current value of an XML-attribute.
 */
public class XMLAttribute {

    @NotNull
    private final String key;
    @NotNull
    private final String originalValue;
    @Nullable
    private String value;

    /**
     * Construct an new XML-attribute using the given key and value.
     *
     * @param key           The name of this XML-attribute.
     * @param originalValue The original value of this XML-attribute. (Will also initialize the current value)
     */
    XMLAttribute(@Nullable String key, @Nullable String originalValue) {
        this.key = key == null ? "" : key;
        this.originalValue = originalValue == null ? "" : originalValue;
        this.value = originalValue;
    }

    /**
     * Returns the name of the attribute.
     *
     * @return The name of the attribute.
     */
    @NotNull
    String getKey() {
        return key;
    }

    /**
     * Returns the original value of the attribute.
     *
     * @return The original value of the attribute.
     */
    @NotNull
    String getOriginalValue() {
        return originalValue;
    }

    /**
     * Returns the current value of the attribute.
     *
     * @return The current value of the attribute.
     */
    @NotNull
    public String getValue() {
        if (value == null) {
            this.value = "";
        }

        return this.value;
    }

    /**
     * Alters the current value of the attribute.
     *
     * @param value The current value of the attribute.
     */
    public void setValue(@Nullable String value) {
        this.value = value;
    }

}

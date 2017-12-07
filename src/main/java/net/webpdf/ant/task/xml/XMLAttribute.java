package net.webpdf.ant.task.xml;

/**
 * An instance of this class stores key, original- and current value of an XML-attribute.
 */
public class XMLAttribute {
    private final String key;
    private final String originalValue;
    private String value;

    /**
     * Construct an new XML-attribute using the given key and value.
     *
     * @param key           The name of this XML-attribute.
     * @param originalValue The original value of this XML-attribute. (Will also initialize the current value)
     */
    XMLAttribute(String key, String originalValue) {
        this.key = key == null ? "" : key;
        this.originalValue = originalValue == null ? "" : originalValue;
        this.value = originalValue;
    }

    /**
     * Returns the name of the attribute.
     *
     * @return The name of the attribute.
     */
    String getKey() {
        return key;
    }

    /**
     * Returns the original value of the attribute.
     *
     * @return The original value of the attribute.
     */
    String getOriginalValue() {
        return originalValue;
    }

    /**
     * Returns the current value of the attribute.
     *
     * @return The current value of the attribute.
     */
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
    public void setValue(String value) {
        this.value = value;
    }
}

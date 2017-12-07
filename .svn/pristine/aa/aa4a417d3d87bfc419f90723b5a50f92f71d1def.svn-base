package net.webpdf.ant.task.xml;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class XMLAttributeTest {

    @Test
    public void attributeTest() throws Exception {
        XMLAttribute attribute = new XMLAttribute("key", "originalValue");
        assertEquals(String.format("Unexpected key: %s", attribute.getKey()),
            "key", attribute.getKey());
        assertEquals(String.format("Unexpected original value: %s", attribute.getOriginalValue()),
            "originalValue", attribute.getOriginalValue());
        assertEquals(String.format("Unexpected value: %s", attribute.getValue()),
            "originalValue", attribute.getValue());
        attribute.setValue("value");
        assertEquals(String.format("Unexpected value: %s", attribute.getValue()),
            "value", attribute.getValue());
    }

    @Test
    public void attributeNullTest() throws Exception {
        XMLAttribute attribute = new XMLAttribute(null, null);
        assertEquals(String.format("Unexpected key: %s", attribute.getKey()),
            "", attribute.getKey());
        assertEquals(String.format("Unexpected original value: %s", attribute.getOriginalValue()),
            "", attribute.getOriginalValue());
        assertEquals(String.format("Unexpected value: %s", attribute.getValue()),
            "", attribute.getValue());
        attribute.setValue(null);
        assertEquals(String.format("Unexpected value: %s", attribute.getValue()),
            "", attribute.getValue());
    }
}

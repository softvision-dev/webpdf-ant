package net.webpdf.ant.task.xml;

import junitparams.JUnitParamsRunner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.UnknownElement;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.namespace.QName;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class XMLElementTest {

    @Test
    public void testParseUnknownElement() {
        UnknownElement unknownElement = new UnknownElement("element");
        RuntimeConfigurable rt = new RuntimeConfigurable(unknownElement, "someTask");
        rt.setAttribute("attr1", "1");
        rt.setAttribute("attr2", "2");
        rt.setAttribute("attrInt", 2);
        unknownElement.setRuntimeConfigurableWrapper(rt);

        UnknownElement unknownChild = new UnknownElement("child");
        rt = new RuntimeConfigurable(unknownChild, "someTask");
        rt.setAttribute("attr3", "3");
        rt.setAttribute("attr4", "4");
        rt.setAttribute("attrInt", 4);
        unknownChild.setRuntimeConfigurableWrapper(rt);

        unknownElement.addChild(unknownChild);

        XMLElement xmlElement = XMLElement.parseUnknownElement(unknownElement);
        xmlElement.getAttributes();
        assertEquals(String.format("Unexpected child count %s", xmlElement.getChildren().size()),
            1, xmlElement.getChildren().size());
        XMLElement child = xmlElement.getChildren().get(0).getValue();
        assertNotNull("Valid child expected.", child);
        //contain all attributes of type String:
        assertEquals(String.format("Unexpected attribute attr1 : %s", xmlElement.getAttributes().get(new QName("attr1"))),
            "1", xmlElement.getAttributes().get(new QName("attr1")));
        assertEquals(String.format("Unexpected attribute attr1 : %s", xmlElement.getAttributes().get(new QName("attr2"))),
            "2", xmlElement.getAttributes().get(new QName("attr2")));
        assertEquals(String.format("Unexpected attribute attr3 : %s", child.getAttributes().get(new QName("attr3"))),
            "3", child.getAttributes().get(new QName("attr3")));
        assertEquals(String.format("Unexpected attribute attrInt : %s", child.getAttributes().get(new QName("attr4"))),
            "4", child.getAttributes().get(new QName("attr4")));
        //ignore all attributes of other type than String:
        assertNull(String.format("Unexpected attribute attrInt : %s", xmlElement.getAttributes().get(new QName("attrInt"))), xmlElement.getAttributes().get(new QName("attrInt")));
        assertNull(String.format("Unexpected attribute attrInt : %s", child.getAttributes().get(new QName("attrInt"))), xmlElement.getAttributes().get(new QName("attrInt")));
    }

    @Test
    public void testParseNull() {
        assertNotNull("Null should at least result in an empty element!", XMLElement.parseUnknownElement(null));
    }

    @Test
    public void testPrepareConfiguration() throws Exception {
        Project project = new Project();
        //project property should be used to replace lang var.
        project.setNewProperty("lang", "eng");

        UnknownElement unknownElement = new UnknownElement("operation");
        RuntimeConfigurable rt = new RuntimeConfigurable(unknownElement, "operation");
        unknownElement.setRuntimeConfigurableWrapper(rt);

        UnknownElement ocr = new UnknownElement("ocr");
        rt = new RuntimeConfigurable(ocr, "operation");
        rt.setAttribute("language", "${lang}");
        rt.setAttribute("outputFormat", "text");
        rt.setAttribute("checkResolution", "true");
        rt.setAttribute("imageDpi", "200");
        rt.setAttribute("forceEachPage", "true");
        ocr.setRuntimeConfigurableWrapper(rt);

        unknownElement.addChild(ocr);

        XMLElement xmlElement = XMLElement.parseUnknownElement(unknownElement);
        xmlElement.prepareConfiguration(project);
        assertEquals(String.format("Unexpected child count %s", xmlElement.getChildren().size()),
            1, xmlElement.getChildren().size());
        XMLElement child = xmlElement.getChildren().get(0).getValue();
        assertNotNull("Valid child expected.", child);

        //lang should have been replaced according to project properties.
        assertEquals(String.format("Unexpected value for attribute language: %s", child.getAttributes().get(new QName("language"))), "eng", child.getAttributes().get(new QName("language")));
    }
}

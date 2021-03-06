package net.webpdf.ant.task.xml;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.UnknownElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.util.*;

import static net.webpdf.ant.task.xml.XMLElement.OPERATION_NAMESPACE;

/**
 * Collects the XML content of an operation tag and provides the means to parse it to a XML String.
 */
@XmlRootElement(namespace = OPERATION_NAMESPACE)
public class XMLElement {

    static final String OPERATION_NAMESPACE = "http://schema.webpdf.de/1.0/operation";

    @NotNull
    private String xmlTag = "";
    @NotNull
    private final List<XMLElement> children = new ArrayList<>();
    @NotNull
    private final List<XMLAttribute> attributes = new ArrayList<>();

    /**
     * An instance representing a generic XML node.
     */
    public XMLElement() {
    }

    /**
     * Initialize the XML node from an unknown ANT element.
     *
     * @param unknownElement The element this node shall be initialized with.
     */
    private void initElement(@Nullable UnknownElement unknownElement) {
        if (unknownElement == null) {
            return;
        }
        this.xmlTag = unknownElement.getTag() == null ? "" : unknownElement.getTag();

        //GET CHILDREN
        if (unknownElement.getChildren() != null) {
            for (UnknownElement unknownChildElement : unknownElement.getChildren()) {
                XMLElement childElement = new XMLElement();
                childElement.initElement(unknownChildElement);
                children.add(childElement);
            }
        }

        //GET ATTRIBUTES
        if (unknownElement.getWrapper() != null) {
            for (Map.Entry<String, Object> attribute : unknownElement.getWrapper().getAttributeMap().entrySet()) {
                if (attribute.getValue() instanceof String) {
                    attributes.add(new XMLAttribute(attribute.getKey(), (String) attribute.getValue()));
                }
            }
        }
    }

    /**
     * A generic access method providing JAXB with all attributes, that have been set for this node.
     *
     * @return All attributes, that have been set for this node.
     */
    @XmlAnyAttribute
    @JaxBAccess
    @NotNull
    public Map<QName, String> getAttributes() {
        Map<QName, String> elements = new HashMap<>();
        for (XMLAttribute attribute : attributes) {
            elements.put(new QName(attribute.getKey()), attribute.getValue());
        }
        return elements;
    }

    /**
     * A generic access method providing JAXB with all child nodes, that are contained in this node.
     * (Will create child nodes via recursive instantiation.)
     *
     * @return All child nodes, that are contained by this node.
     */
    @XmlAnyElement
    @JaxBAccess
    @NotNull
    public List<JAXBElement<XMLElement>> getChildren() {
        List<JAXBElement<XMLElement>> childNodes = new ArrayList<>();
        for (XMLElement element : children) {
            childNodes.add(new JAXBElement<>(new QName(OPERATION_NAMESPACE, element.xmlTag, "ns3"), XMLElement.class, element));
        }
        return childNodes;
    }

    /**
     * Extracts all known attributes from the given tag and therefore initializes the state of this node.
     *
     * @throws BuildException shall be thrown if the projects properties could not be read.
     */
    private void initializeAttributes(@Nullable Project project) throws BuildException {
        if (project != null) {
            for (XMLAttribute attribute : attributes) {
                attribute.setValue(project.replaceProperties(attribute.getOriginalValue()));
            }
            for (XMLElement element : children) {
                element.initializeAttributes(project);
            }
        }
    }

    /**
     * Uses JAXB to marshall this node and it's children to XML.
     *
     * @return A container collecting the operation and options defined by this node.
     * @throws JAXBException Shall be thrown if an error occurred during JAXB marshalling/unmarshalling.
     */
    @NotNull
    public String prepareConfiguration(@Nullable Project project) throws JAXBException {
        initializeAttributes(project);
        StringWriter writer = new StringWriter();

        //MARSHAL GENERIC OBJECT TO XML
        Marshaller marshaller = JAXBContext.newInstance(XMLElement.class).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(
            new JAXBElement<>(new QName(OPERATION_NAMESPACE, xmlTag, "ns3"), XMLElement.class, this), writer);

        return writer.toString();
    }

    /**
     * This static access method allows to parse an unknown ANT element to an executable OperationTask, that is
     * representing a call to a webPDF webservice. (a specific operation)
     *
     * @param unknownElement The element that shall be parsed.
     * @return A XMLElement containing structured generic xml content.
     */
    @NotNull
    public static XMLElement parseUnknownElement(@Nullable UnknownElement unknownElement) {
        XMLElement element = new XMLElement();
        element.initElement(unknownElement);
        return element;
    }

}

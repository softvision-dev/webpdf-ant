package net.webpdf.ant;

import net.webpdf.ant.task.Task;
import net.webpdf.ant.task.TaskName;
import net.webpdf.ant.task.logging.LogTag;
import net.webpdf.ant.task.xml.XMLElement;
import net.webpdf.wsclient.WebService;
import net.webpdf.wsclient.WebServiceFactory;
import net.webpdf.wsclient.WebServiceProtocol;
import net.webpdf.wsclient.documents.SoapDocument;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import org.apache.http.auth.Credentials;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.filters.StringInputStream;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

/**
 * Task mapping the ant <operation> tag and all it's options.
 */
public class OperationTask extends Task {

    @Nullable
    private final XMLElement xmlElement;

    /**
     * Each operation task shall represent one call to a webPDF webservice and therefore shall contain all necessary
     * options in a XML substructure, that is conforming to the XSD schema defined by the webPDF server.
     * Unknown sub elements of this task shall be treated as pure XML content and shall not be wrapped in Task instances.
     */
    OperationTask(@Nullable XMLElement xmlElement, @Nullable Project project) {
        super(TaskName.OPERATION);
        this.xmlElement = xmlElement;
        setProject(project);
    }

    /**
     * Central method to execute the operation. The containing task shall prepare and execute the call of this method
     * iteratively - This method represents the execution of a request to a webPDF webservice and is expecting, that the
     * context of this request has been prepared previously.
     */
    @Override
    public void execute() throws BuildException {
        try {
            if (xmlElement == null || getFiles() == null || getProject() == null) {
                throw new BuildException("The operation tasks definition is incomplete");
            }
            try (
                Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP, getTaskConfiguration().getServerURL());
                InputStream xmlInputStream = new StringInputStream(xmlElement.prepareConfiguration(getProject()));
                SoapDocument soapDocument = new SoapDocument(getFiles().getCurrentSource().toURI(), getFiles().getCurrentTarget())
            ) {
                Credentials credentials = getTaskConfiguration().getCredentials();
                if (credentials != null) {
                    session.setCredentials(credentials);
                }
                WebService<SoapDocument, ?, SoapDocument> webservice = WebServiceFactory.createInstance(session, new StreamSource(xmlInputStream));
                getLogger().info(webservice.getClass().getSimpleName(), LogTag.OPERATION);
                webservice.setDocument(soapDocument);
                webservice.process();
            }
        } catch (ResultException ex) {
            getFiles().dropCurrentTarget();
            throw new BuildException(ex.getResult().getMessage() + " [EC " + ex.getResult().getCode() + "]", ex);
        } catch (JAXBException | IOException ex) {
            getFiles().dropCurrentTarget();
            throw new BuildException(ex.getMessage(), ex);
        }
    }

}

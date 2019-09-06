package net.webpdf.ant;

import net.webpdf.ant.task.TaskConfiguration;
import net.webpdf.ant.task.credentials.UserCredentials;
import net.webpdf.ant.task.files.IterativeTaskFile;
import net.webpdf.ant.task.files.TempDir;
import net.webpdf.ant.task.xml.XMLElement;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.UnknownElement;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.xml.bind.JAXBException;
import java.io.File;

import static org.junit.Assert.*;

public class OperationTaskIntegrationTest {

    private final TestResources testResources = new TestResources(OperationTaskIntegrationTest.class);
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private OperationTask operationTask;

    private void setup(String serverURL, XMLElement xmlElement, Project project) {
        TaskConfiguration taskConfiguration = new TaskConfiguration();
        operationTask = new OperationTask(xmlElement, project);
        operationTask.setTaskConfiguration(taskConfiguration);
        operationTask.getTaskConfiguration().setServerUrl(serverURL);
    }

    @Test
    public void testConverterOperation() throws Exception {
        File testFile = testResources.getResource("test.pdf");
        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        converter.getWrapper().setAttribute("pages", "1");
        converter.getWrapper().setAttribute("compression", "false");
        converter.getWrapper().setAttribute("jpegQuality", "100");
        converter.getWrapper().setAttribute("reduceResolution", "true");
        XMLElement xmlElement = XMLElement.parseUnknownElement(operation);

        setup(testResources.getArguments().buildServerUrl().toString(), xmlElement, new Project());
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(testFile, "targetFileName", new TempDir());
        operationTask.setFiles(iterativeTaskFile);
        operationTask.execute();

        assertNotNull(operationTask.getFiles());
        Assert.assertTrue("Output file should have been created.", operationTask.getFiles().getCurrentTarget().exists());
    }

    @Test
    public void testCredentials() throws Exception {
        File testFile = testResources.getResource("test.pdf");
        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        converter.getWrapper().setAttribute("pages", "1");
        converter.getWrapper().setAttribute("compression", "false");
        converter.getWrapper().setAttribute("jpegQuality", "100");
        converter.getWrapper().setAttribute("reduceResolution", "true");
        XMLElement xmlElement = XMLElement.parseUnknownElement(operation);

        setup(testResources.getArguments().buildServerUrl().toString(), xmlElement, new Project());
        assertTrue("Fail on error should have been initialized with true.", operationTask.getTaskConfiguration().isFailOnError());
        operationTask.getTaskConfiguration().setFailOnError(false);
        assertFalse("Fail on error should have been set to false.", operationTask.getTaskConfiguration().isFailOnError());

        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(testFile, "targetFileName", new TempDir());
        operationTask.setFiles(iterativeTaskFile);
        assertNotNull("Task files should have been set.", operationTask.getTaskConfiguration().getTaskFiles());

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setUsername("admin");
        userCredentials.setPassword("admin");
        operationTask.getTaskConfiguration().setCredentialsTask(userCredentials);
        operationTask.execute();

        assertNotNull(operationTask.getFiles());
        Assert.assertTrue("Output file should have been created.", operationTask.getFiles().getCurrentTarget().exists());
    }

    @Test(expected = BuildException.class)
    public void testInvalidOperationData() throws Exception {
        File testFile = testResources.getResource("test.pdf");
        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        converter.getWrapper().setAttribute("invalid", "invalid");
        XMLElement xmlElement = XMLElement.parseUnknownElement(operation);

        setup(testResources.getArguments().buildServerUrl().toString(), xmlElement, new Project());
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(testFile, "targetFileName", new TempDir());
        operationTask.setFiles(iterativeTaskFile);
        operationTask.execute();
    }

    @Test(expected = BuildException.class)
    public void testNullElement() throws Exception {
        File testFile = testResources.getResource("test.pdf");

        setup(testResources.getArguments().buildServerUrl().toString(), null, new Project());
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(testFile, "targetFileName", new TempDir());
        operationTask.setFiles(iterativeTaskFile);
        operationTask.execute();
    }

    @Test(expected = BuildException.class)
    public void testNullProject() throws Exception {
        File testFile = testResources.getResource("test.pdf");
        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        converter.getWrapper().setAttribute("pages", "1");
        converter.getWrapper().setAttribute("compression", "false");
        converter.getWrapper().setAttribute("jpegQuality", "100");
        converter.getWrapper().setAttribute("reduceResolution", "true");
        XMLElement xmlElement = XMLElement.parseUnknownElement(operation);

        setup(testResources.getArguments().buildServerUrl().toString(), xmlElement, null);
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(testFile, "targetFileName", new TempDir());
        operationTask.setFiles(iterativeTaskFile);
        operationTask.execute();
    }

    @Test(expected = BuildException.class)
    public void testNullFile() throws Exception {
        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        converter.getWrapper().setAttribute("pages", "1");
        converter.getWrapper().setAttribute("compression", "false");
        converter.getWrapper().setAttribute("jpegQuality", "100");
        converter.getWrapper().setAttribute("reduceResolution", "true");
        XMLElement xmlElement = XMLElement.parseUnknownElement(operation);

        setup(testResources.getArguments().buildServerUrl().toString(), xmlElement, new Project());
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(null, "targetFileName", new TempDir());
        operationTask.setFiles(iterativeTaskFile);
        operationTask.execute();
    }

    @Test(expected = BuildException.class)
    public void unsetTaskFiles() throws Exception {
        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        converter.getWrapper().setAttribute("pages", "1");
        converter.getWrapper().setAttribute("compression", "false");
        converter.getWrapper().setAttribute("jpegQuality", "100");
        converter.getWrapper().setAttribute("reduceResolution", "true");
        XMLElement xmlElement = XMLElement.parseUnknownElement(operation);

        setup(testResources.getArguments().buildServerUrl().toString(), xmlElement, new Project());
        operationTask.execute();
    }

    @Test(expected = BuildException.class)
    public void testInvalidXMLElement() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(), new TestInvalidXMLElement(), new Project());
        File testFile = testResources.getResource("test.pdf");
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(testFile, "targetFileName", new TempDir());
        operationTask.setFiles(iterativeTaskFile);
        operationTask.execute();
    }

    private static class TestInvalidXMLElement extends XMLElement {
        @Override
        @NotNull
        public String prepareConfiguration(Project project) throws JAXBException {
            throw new JAXBException("For testing reasons.");
        }
    }
}
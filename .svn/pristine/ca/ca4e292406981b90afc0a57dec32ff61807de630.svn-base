package net.webpdf.ant;

import net.webpdf.ant.task.TaskConfiguration;
import net.webpdf.ant.task.credentials.NTCredentials;
import net.webpdf.ant.task.credentials.UserCredentials;
import net.webpdf.ant.task.variable.Variable;
import net.webpdf.ant.task.variable.VariableRole;
import net.webpdf.ant.task.xml.XMLElement;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileNameMapper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.*;

public class WebPDFTaskIntegrationTest {

    private final TestResources testResources = new TestResources(WebPDFTaskIntegrationTest.class);
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Project project;
    private WebPDFTask webPDFTask;
    private GroupTask groupTask;
    private TaskConfiguration taskConfiguration;
    private Variable outputVariable;
    private File targetFile;

    private void setup(String serverUrl, File targetFile) throws Exception {
        this.targetFile = targetFile;
        project = new Project();
        taskConfiguration = new TaskConfiguration();
        groupTask = new GroupTask();
        groupTask.setProject(project);
        groupTask.setTaskConfiguration(taskConfiguration);
        webPDFTask = new WebPDFTask();
        webPDFTask.setTaskConfiguration(taskConfiguration);
        webPDFTask.setFailOnError(true);
        webPDFTask.setServerUrl(serverUrl);
        webPDFTask.add(groupTask);
        webPDFTask.setProject(project);
        webPDFTask.getTaskConfiguration().setServerUrl(serverUrl);
        webPDFTask.setTempDir(temporaryFolder.newFolder());
        webPDFTask.setTargetFile(targetFile);
        webPDFTask.add(new TestFileNameMapper());
        outputVariable = new Variable();
        outputVariable.setRole(VariableRole.OUTPUT);
        outputVariable.setName("output");
        outputVariable.setOwningTask(webPDFTask);
        webPDFTask.add(outputVariable);
    }

    @Test
    public void testWebPDFTask() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt"));
        File testFile = testResources.getResource("test.pdf");
        webPDFTask.add(new FileResource(testFile));

        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        groupTask.add(operation);

        webPDFTask.addTask(new Echo());
        webPDFTask.execute();
        Assert.assertTrue("Output file should have been created.", new File(webPDFTask.getVariables().getVar(VariableRole.OUTPUT).getValue()).exists());
    }

    @Test
    public void testVariables() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            temporaryFolder.newFile());
        File testFile = testResources.getResource("test.pdf");

        Variable inputVariable = new Variable();
        inputVariable.setOwningTask(webPDFTask);
        inputVariable.setName("input");
        inputVariable.setRole(VariableRole.INPUT);
        inputVariable.setValue(testFile.getAbsolutePath());
        webPDFTask.add(inputVariable);

        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        groupTask.add(operation);

        webPDFTask.addTask(new Echo());
        webPDFTask.add(groupTask);
        webPDFTask.execute();

        Assert.assertTrue("Output file should have been created.", new File(webPDFTask.getVariables().getVar(VariableRole.OUTPUT).getValue()).exists());
    }

    @Test
    public void testTargetDirectory() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt"));
        File testFile = testResources.getResource("test.pdf");

        webPDFTask.setTargetDir(temporaryFolder.getRoot());
        webPDFTask.add(new FileResource(testFile));

        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        groupTask.add(operation);

        webPDFTask.addTask(new Echo());
        webPDFTask.add(groupTask);
        webPDFTask.execute();
        Assert.assertTrue("Output file should have been created.", new File(webPDFTask.getVariables().getVar(VariableRole.OUTPUT).getValue()).exists());
    }

    @Test
    public void testUserCredentials() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt"));
        File testFile = testResources.getResource("test.pdf");
        webPDFTask.add(new FileResource(testFile));

        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        UserCredentials userCredentials = new UserCredentials();
        String userName = testResources.getArguments().getUsername() != null ?
                              testResources.getArguments().getUsername() :
                              "admin";
        String password = testResources.getArguments().getPassword() != null ?
                              testResources.getArguments().getPassword() :
                              "admin";
        userCredentials.setUsername(userName);
        userCredentials.setPassword(password);
        webPDFTask.add(userCredentials);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        groupTask.add(operation);

        webPDFTask.execute();
        Assert.assertTrue("Output file should have been created.", new File(webPDFTask.getVariables().getVar(VariableRole.OUTPUT).getValue()).exists());
    }

    @Test
    public void testNTCredentials() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt"));
        File testFile = testResources.getResource("test.pdf");

        NTCredentials ntCredentials = new NTCredentials();
        String userName = testResources.getArguments().getUsername() != null ?
                              testResources.getArguments().getUsername() :
                              "admin";
        String password = testResources.getArguments().getPassword() != null ?
                              testResources.getArguments().getPassword() :
                              "admin";
        ntCredentials.setUsername(userName);
        ntCredentials.setPassword(password);
        webPDFTask.add(ntCredentials);
        webPDFTask.add(new FileResource(testFile));

        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        groupTask.add(operation);

        webPDFTask.execute();
        Assert.assertTrue("Output file should have been created.", new File(webPDFTask.getVariables().getVar(VariableRole.OUTPUT).getValue()).exists());
    }

    @Test
    public void testSkipFailingTask() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt"));
        File testFile = testResources.getResource("test.pdf");
        webPDFTask.add(new FileResource(testFile));
        webPDFTask.setFailOnError(false);

        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);
        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        converter.getRuntimeConfigurableWrapper().setAttribute("invalid", "invalid");

        UnknownElement operation2 = new UnknownElement("operation");
        UnknownElement converter2 = new UnknownElement("converter");
        operation2.addChild(converter2);
        RuntimeConfigurable runtimeConfigurable2 = new RuntimeConfigurable(converter2, "converter");
        converter2.setRuntimeConfigurableWrapper(runtimeConfigurable2);

        groupTask.add(operation);
        groupTask.add(operation2);

        webPDFTask.execute();
        Assert.assertTrue("Output file should have been created.", new File(webPDFTask.getVariables().getVar(VariableRole.OUTPUT).getValue()).exists());
    }

    @Test(expected = BuildException.class)
    public void failOnGroup() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt"));
        File testFile = testResources.getResource("test.pdf");
        webPDFTask.add(new FileResource(testFile));
        groupTask.setTaskConfiguration(null);

        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);
        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);

        groupTask.add(operation);

        webPDFTask.execute();
    }

    @Test
    public void skipFailingGroup() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt"));
        File testFile = testResources.getResource("test.pdf");
        webPDFTask.add(new FileResource(testFile));
        webPDFTask.setFailOnError(false);
        groupTask.setTaskConfiguration(null);

        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);
        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        groupTask.add(operation);

        UnknownElement operation2 = new UnknownElement("operation");
        UnknownElement converter2 = new UnknownElement("converter");
        operation2.addChild(converter2);
        RuntimeConfigurable runtimeConfigurable2 = new RuntimeConfigurable(converter2, "converter");
        converter2.setRuntimeConfigurableWrapper(runtimeConfigurable2);
        GroupTask groupTask2 = new GroupTask();
        groupTask2.setProject(project);
        groupTask2.setTaskConfiguration(taskConfiguration);
        groupTask2.add(operation2);

        webPDFTask.add(groupTask2);
        webPDFTask.execute();
        Assert.assertTrue("Output file should have been created.", new File(webPDFTask.getVariables().getVar(VariableRole.OUTPUT).getValue()).exists());
    }

    @Test(expected = BuildException.class)
    public void testInvalidTargetDir() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt"));
        webPDFTask.setTargetDir(null);
    }

    @Test(expected = BuildException.class)
    public void testInvalidTempDir() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt"));
        webPDFTask.setTempDir(null);
    }

    @Test(expected = BuildException.class)
    public void testAddOperationDirectly() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt"));
        assertNotNull(webPDFTask.getTaskConfiguration());
        webPDFTask.addTask(new OperationTask(XMLElement.parseUnknownElement(new UnknownElement("operation")), webPDFTask.getProject()));
    }

    @Test
    public void testAddOperationDirectlyWithoutFailure() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt"));
        File testFile = testResources.getResource("test.pdf");
        assertNotNull(webPDFTask.getTaskConfiguration());
        webPDFTask.setFailOnError(false);
        //MUST NOT FAIL AFTER THE FOLLOWING ERRONEOUS ASSIGNMENT:
        webPDFTask.addTask(new OperationTask(XMLElement.parseUnknownElement(new UnknownElement("operation")), webPDFTask.getProject()));
        webPDFTask.add(new FileResource(testFile));

        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        groupTask.add(operation);

        webPDFTask.execute();
        Assert.assertTrue("Output file should have been created.", new File(webPDFTask.getVariables().getVar(VariableRole.OUTPUT).getValue()).exists());
    }

    @Test
    public void testUninitializedFiles() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt"));

        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        groupTask.add(operation);

        webPDFTask.execute();
        Assert.assertFalse("Output file should not have been created.", targetFile.exists());
    }

    private class TestFileNameMapper implements FileNameMapper {
        private String fromName = "";

        @Override
        public void setFrom(String from) {
            this.fromName = from;
        }

        @Override
        public void setTo(String to) {

        }

        @Override
        public String[] mapFileName(String sourceFileName) {
            return new String[]{fromName + "out"};
        }
    }
}
package net.webpdf.ant;

import net.webpdf.ant.task.TaskConfiguration;
import net.webpdf.ant.task.files.IterativeTaskFile;
import net.webpdf.ant.task.files.TempDir;
import net.webpdf.ant.task.variable.Variable;
import net.webpdf.ant.task.variable.VariableRole;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.UnknownElement;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.*;

public class GroupTaskIntegrationTest {

    private final TestResources testResources = new TestResources(GroupTaskIntegrationTest.class);
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Project project;
    private GroupTask groupTask;
    private TaskConfiguration taskConfiguration;
    private Variable outputVariable;

    private void setup(String serverUrl, String outputValue) throws Exception {
        project = new Project();
        taskConfiguration = new TaskConfiguration();
        groupTask = new GroupTask();
        groupTask.setProject(project);
        groupTask.setTaskConfiguration(taskConfiguration);
        groupTask.getTaskConfiguration().setServerUrl(serverUrl);
        outputVariable = new Variable();
        outputVariable.setRole(VariableRole.OUTPUT);
        outputVariable.setName("output");
        outputVariable.setValue(outputValue);
        outputVariable.setOwningTask(groupTask);
        groupTask.add(outputVariable);
    }

    @Test
    public void testGroupTaskOperationElement() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt").getAbsolutePath());
        File testFile = testResources.getResource("test.pdf");

        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        groupTask.add(operation);
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(testFile, "targetFileName", new TempDir());
        groupTask.setFiles(iterativeTaskFile);

        assertEquals("Set variable should be identical to mapped variable", outputVariable,
            groupTask.getVariables().getVar(VariableRole.OUTPUT));

        groupTask.execute();
        Assert.assertTrue("Output file should have been created.", groupTask.getFiles().getCurrentTarget().exists());
    }

    @Test
    public void testGroupTaskOperationTask() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt").getAbsolutePath());
        File testFile = testResources.getResource("test.pdf");

        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);

        Variable variable = new Variable();
        variable.setRole(VariableRole.INPUT);
        variable.setName("input");
        variable.setValue(testFile.getAbsolutePath());
        groupTask.add(variable);

        groupTask.addTask(operation);
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(testFile, "targetFileName", new TempDir());
        groupTask.setFiles(iterativeTaskFile);

        Variable setVariable = groupTask.getVariables().getVar(VariableRole.OUTPUT);
        assertEquals("Set variable should be identical to mapped variable", outputVariable, setVariable);

        groupTask.execute();
        Assert.assertTrue("Output file should have been created.", groupTask.getFiles().getCurrentTarget().exists());
    }

    @Test(expected = BuildException.class)
    public void setNullInputVariable() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt").getAbsolutePath());
        File testFile = testResources.getResource("test.pdf");

        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        groupTask.add(operation);

        Variable variable = new Variable();
        variable.setRole(VariableRole.INPUT);
        variable.setName("input");
        variable.setValue(null);
        groupTask.add(variable);

        groupTask.addTask(operation);
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(testFile, "targetFileName", new TempDir());
        groupTask.setFiles(iterativeTaskFile);

        Variable setVariable = groupTask.getVariables().getVar(VariableRole.INPUT);
        assertEquals("Set variable should be identical to mapped variable", variable, setVariable);

        groupTask.execute();
    }

    @Test
    public void setNullOutputVariable() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(), null);
        File testFile = testResources.getResource("test.pdf");

        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        groupTask.add(operation);

        groupTask.addTask(operation);
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(testFile, "targetFileName", new TempDir());
        groupTask.setFiles(iterativeTaskFile);

        Variable setVariable = groupTask.getVariables().getVar(VariableRole.OUTPUT);
        assertEquals("Set variable should be identical to mapped variable", outputVariable, setVariable);

        groupTask.execute();
        Assert.assertTrue("Output file should have been created.", groupTask.getFiles().getCurrentTarget().exists());
    }

    @Test(expected = BuildException.class)
    public void inexistentSourceFile() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt").getAbsolutePath());
        File testFile = new File("inexistent.pdf");

        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        groupTask.add(operation);

        Variable variable = new Variable();
        variable.setRole(VariableRole.INPUT);
        variable.setName("input");
        variable.setValue("");
        groupTask.add(variable);

        groupTask.addTask(operation);
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(testFile, "targetFileName", new TempDir());
        groupTask.setFiles(iterativeTaskFile);

        Variable setVariable = groupTask.getVariables().getVar(VariableRole.OUTPUT);
        assertEquals("Set variable should be identical to mapped variable", outputVariable, setVariable);

        groupTask.execute();
    }

    @Test
    public void testSkipInexistentSourceFile() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt").getAbsolutePath());
        File testFile = testResources.getResource("test.pdf");

        Variable variable = new Variable();
        variable.setRole(VariableRole.INPUT);
        variable.setName("input");
        variable.setValue("");
        groupTask.add(variable);

        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(testFile, "targetFileName", new TempDir());
        groupTask.getTaskConfiguration().setFailOnError(false);
        groupTask.setFiles(iterativeTaskFile);
        groupTask.execute();
    }

    @Test(expected = BuildException.class)
    public void testErrorInOperation() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt").getAbsolutePath());
        File testFile = new File("test.pdf");

        UnknownElement operation = new UnknownElement("operation");
        UnknownElement converter = new UnknownElement("converter");
        operation.addChild(converter);

        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(converter, "converter");
        converter.setRuntimeConfigurableWrapper(runtimeConfigurable);
        converter.getWrapper().setAttribute("invalid", "invalid");
        groupTask.add(operation);

        groupTask.addTask(operation);
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(testFile, "targetFileName", new TempDir());
        groupTask.setFiles(iterativeTaskFile);

        groupTask.execute();
    }

    @Test(expected = BuildException.class)
    public void testNullProject() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt").getAbsolutePath());
        File testFile = testResources.getResource("test.pdf");

        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(testFile, "targetFileName", new TempDir());
        groupTask.setFiles(iterativeTaskFile);
        groupTask.setProject(null);
        groupTask.execute();
    }

    @Test(expected = BuildException.class)
    public void testNullFile() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt").getAbsolutePath());
        File testFile = null;

        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(testFile, "targetFileName", new TempDir());
        groupTask.setFiles(iterativeTaskFile);
        groupTask.execute();
    }

    @Test(expected = BuildException.class)
    public void unsetTaskFiles() throws Exception {
        setup(testResources.getArguments().buildServerUrl().toString(),
            new File(temporaryFolder.getRoot(), "value.txt").getAbsolutePath());
        groupTask.execute();
    }
}
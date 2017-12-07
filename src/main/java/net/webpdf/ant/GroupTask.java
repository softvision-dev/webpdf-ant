package net.webpdf.ant;

import net.webpdf.ant.task.AntAccess;
import net.webpdf.ant.task.Task;
import net.webpdf.ant.task.TaskName;
import net.webpdf.ant.task.logging.LogTag;
import net.webpdf.ant.task.variable.Variable;
import net.webpdf.ant.task.variable.VariableRole;
import net.webpdf.ant.task.xml.XMLElement;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Task mapping the ant <group> tag and all it's options.
 */
public class GroupTask extends Task implements TaskContainer {

    private List<OperationTask> operations = new ArrayList<>();

    /**
     * A Group Task shall be able to contain individual operations, that are bundled to one sequential groups, defining
     * common rules and resources.
     */
    @AntAccess
    public GroupTask() {
        super(TaskName.GROUP);
    }

    /**
     * Central method to execute a task and all it's sub tasks. This method shall prepare and define the needed context
     * and shall execute it's sub tasks sequentially in an ordered fashion.
     */
    @Override
    public void execute() {
        getLogger().info(LogTag.GROUP);

        if (getFiles() == null || getTaskConfiguration() == null || getProject() == null) {
            throw new BuildException("The operation tasks definition is incomplete");
        }

        File storedSource = null;
        if (getVariables().getVar(VariableRole.OUTPUT) != null) {
            storedSource = getFiles().getCurrentSource();
            getFiles().preserveCurrentSource();
        }

        initialize();
        processOperations();
        publishResult(storedSource);
    }

    /**
     * Prepare variables and sources for processing.
     */
    private void initialize() {
        //Init file iteration and source
        if (getVariables().isRoleTaken(VariableRole.INPUT)) {
            Variable input = getVariables().getVar(VariableRole.INPUT);
            input.execute();
            File srcFile = new File(getProject().replaceProperties(getProject().getProperty(input.getName())));
            if (!srcFile.exists() || !srcFile.isFile() || !srcFile.canRead()) {
                if (getTaskConfiguration().isFailOnError()) {
                    throw new BuildException("The set source file can not be read: " + input);
                }
                getLogger().warn("The group is skipped, as the set source file can not be read: " + input, LogTag.GROUP);
                return;
            }
            getFiles().setCurrentSource(srcFile, true);
        }
    }

    /**
     * Execute operation sequence.
     */
    private void processOperations() {
        Iterator<OperationTask> iter = operations.iterator();
        while (iter.hasNext()) {
            OperationTask operation = iter.next();
            try {
                operation.setFiles(getFiles());
                operation.setTaskConfiguration(getTaskConfiguration());
                operation.execute();
                if (!getVariables().isRoleTaken(VariableRole.OUTPUT) || iter.hasNext()) {
                    getFiles().prepareNextOperation();
                }
            } catch (BuildException ex) {
                if (getTaskConfiguration().isFailOnError()) {
                    throw ex;
                } else {
                    getLogger().warn(ex.getMessage(), ex, LogTag.GROUP);
                }
            }
        }
    }

    /**
     * Publish result if needed and cleanup.
     *
     * @param storedSource A source file, that shall be restored, after the execution of the current group.
     */
    private void publishResult(File storedSource) {
        if (getVariables().isRoleTaken(VariableRole.OUTPUT) && storedSource != null) {
            getFiles().tryPublish(getVariables().getVar(VariableRole.OUTPUT));
            getFiles().setCurrentSource(storedSource, false);
        }
    }

    /**
     * Method to add sub elements to this task. This shall be used by ant access methods and shall treat each appended
     * element according to it's type.
     *
     * @param task The sub task, that shall be added.
     */
    @Override
    @AntAccess
    public void addTask(org.apache.tools.ant.Task task) {
        if (task instanceof UnknownElement) {
            add((UnknownElement) task);
        }
    }

    /**
     * This method shall define special handling for unknown sub elements. In this context an "unknown element" is most
     * likely an operation tag. It is expected, that unknown elements will be treated as pure XML content
     * of the containing task, instead of representing a task themselves.
     * <p>
     * It is not expected to use this method actively, as it is specifically defined for the access by ANT.
     */
    @AntAccess
    public final void add(UnknownElement task) {
        OperationTask operationTask = new OperationTask(XMLElement.parseUnknownElement(task), getProject());
        operationTask.setLocation(task.getLocation());
        operations.add(operationTask);
    }

    /**
     * Adds a variable to the group context. The variable and it's value shall only be known later on, when the group is
     * executed.
     *
     * @param variable The variable, that shall be added.
     */
    @AntAccess
    public final void add(Variable variable) {
        getVariables().add(variable);
    }
}

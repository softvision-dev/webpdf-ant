package net.webpdf.ant.task;

import net.webpdf.ant.task.files.IterativeTaskFile;
import net.webpdf.ant.task.logging.AntLogger;
import net.webpdf.ant.task.logging.LogTag;
import net.webpdf.ant.task.variable.VariableMap;
import org.apache.tools.ant.BuildException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class shall provide all mechanisms for a containing parent task to initialize and execute it's children.
 */
public abstract class Task extends org.apache.tools.ant.Task implements TaskIntf {

    @NotNull
    private final AntLogger logger;
    @NotNull
    private final VariableMap variables;
    @Nullable
    private TaskConfiguration taskConfiguration;
    @Nullable
    private IterativeTaskFile files;

    /**
     * An extending class shall provide a set of basic mechanisms to initialize and execute it's children.
     */
    public Task(@NotNull TaskName taskName) {
        this.variables = new VariableMap(this);
        this.logger = new AntLogger(this);
        setTaskName(taskName.getName());
        setTaskType(taskName.getType());
    }

    /**
     * Called by the project to let the task do its work. This method may be called more than once, if the task is
     * invoked more than once. For example, if target1 and target2 both depend on target3, then running "ant target1
     * target2" will run all tasks in target3 twice.
     *
     * @throws BuildException if something goes wrong with the build.
     */
    @Override
    public abstract void execute() throws BuildException;

    /**
     * Sets the configuration container managing the iterative file progression in between the encapsulated tasks and
     * sub tasks.
     *
     * @param files The file container this task shall be using.
     */
    @Override
    public void setFiles(@Nullable IterativeTaskFile files) {
        this.files = files;
    }

    /**
     * Returns the container managing the iterative file progression of this task.
     *
     * @return The file container this task is currently using.
     */
    @Override
    @Nullable
    public IterativeTaskFile getFiles() {
        return files;
    }

    /**
     * Passes the top level task configuration on to this sub task.
     *
     * @param taskConfiguration The task configuration of the top level webPDF task.
     */
    @Override
    public void setTaskConfiguration(@Nullable TaskConfiguration taskConfiguration) {
        this.taskConfiguration = taskConfiguration;
    }

    /**
     * Returns the top level task configuration of this sub task.
     *
     * @return The task configuration of the containing top level webPDF task.
     */
    @Override
    @NotNull
    public TaskConfiguration getTaskConfiguration() {
        if (taskConfiguration == null) {
            String message = "Task configuration is missing.";
            getLogger().error(message, LogTag.WEBPDF_TASK);
            throw new BuildException(message, getLocation());
        }
        return taskConfiguration;
    }

    /**
     * Returns the variables used by this task.
     *
     * @return The variables used by this task.
     */
    @Override
    @NotNull
    public VariableMap getVariables() {
        return variables;
    }

    /**
     * Returns the logger used by this task.
     *
     * @return The logger used by this task.
     */
    @Override
    @NotNull
    public AntLogger getLogger() {
        return logger;
    }

}

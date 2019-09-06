package net.webpdf.ant.task;

import net.webpdf.ant.task.files.IterativeTaskFile;
import net.webpdf.ant.task.logging.AntLogger;
import net.webpdf.ant.task.variable.VariableMap;
import org.apache.tools.ant.BuildException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An implementing class shall provide all mechanisms for a containing parent task to initialize and execute it's children.
 */
public interface TaskIntf {

    /**
     * Called by the project to let the task do its work. This method may be
     * called more than once, if the task is invoked more than once.
     * For example,
     * if target1 and target2 both depend on target3, then running
     * "ant target1 target2" will run all tasks in target3 twice.
     *
     * @throws BuildException if something goes wrong with the build.
     */
    void execute() throws BuildException;

    /**
     * Sets the configuration container managing the iterative file progression in between the encapsulated tasks and
     * sub tasks.
     *
     * @param files The file container this task shall be using.
     */
    void setFiles(@Nullable IterativeTaskFile files);

    /**
     * Returns the container managing the iterative file progression of this task.
     *
     * @return The file container this task is currently using.
     */
    @Nullable
    IterativeTaskFile getFiles();

    /**
     * Passes the top level task configuration on to this sub task.
     *
     * @param taskConfiguration The task configuration of the top level webPDF task.
     */
    void setTaskConfiguration(@Nullable TaskConfiguration taskConfiguration);

    /**
     * Returns the top level task configuration of this sub task.
     *
     * @return The task configuration of the containing top level webPDF task.
     */
    @Nullable
    TaskConfiguration getTaskConfiguration();

    /**
     * Returns the variables used by this task.
     *
     * @return The variables used by this task.
     */
    @NotNull
    VariableMap getVariables();

    /**
     * Returns the logger used by this task.
     *
     * @return The logger used by this task.
     */
    @NotNull
    AntLogger getLogger();

}

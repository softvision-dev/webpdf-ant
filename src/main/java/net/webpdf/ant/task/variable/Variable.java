package net.webpdf.ant.task.variable;

import net.webpdf.ant.task.AntAccess;
import net.webpdf.ant.task.Task;
import net.webpdf.ant.task.TaskName;
import org.apache.tools.ant.BuildException;
import org.jetbrains.annotations.Nullable;

/**
 * Manages a task storing a variable property. This solution is entirely base on Ant-contrib Variable and an instance
 * of this class is just a wrapper providing some convenience methods.
 */
public class Variable extends Task {

    @Nullable
    private String name;
    @Nullable
    private String value;
    @Nullable
    private VariableRole role;
    @Nullable
    private Task owningTask;

    /**
     * An instance of this class manages and stores a variable property.
     */
    public Variable() {
        super(TaskName.VARIABLE);
    }

    /**
     * Change the task owning this variable.
     *
     * @param owningTask The task that shall be owning the variable.
     */
    public void setOwningTask(@Nullable Task owningTask) {
        this.owningTask = owningTask;
    }

    /**
     * Attempts to publish the currently stored value for the given variable name.
     *
     * @throws BuildException Shall be thrown, if an error occurred while publishing this variable.
     */
    public void execute() throws BuildException {
        net.sf.antcontrib.property.Variable var = new net.sf.antcontrib.property.Variable();
        if (owningTask != null) {
            var.setProject(owningTask.getProject());
            var.setOwningTarget(owningTask.getOwningTarget());
            var.setLocation(owningTask.getLocation());
            var.setName(name);
            var.setValue(value);
            var.execute();
        }
    }

    /**
     * Alters the referable name of the variable.
     *
     * @param name The referable name of the variable.
     */
    @AntAccess
    public void setName(@Nullable String name) {
        this.name = name;
    }

    /**
     * Returns the referable name of the variable.
     *
     * @return The referable name of the variable.
     */
    @AntAccess
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Alters the value of the variable.
     *
     * @param value The value of the variable.
     */
    @AntAccess
    public void setValue(@Nullable String value) {
        this.value = value;
    }

    /**
     * Returns the value of the variable.
     *
     * @return The value of the variable.
     */
    @AntAccess
    @Nullable
    public String getValue() {
        return value;
    }

    /**
     * Alters the role of the variable and provides information about it's usage and purpose via the VariableRole enum.
     *
     * @param role The role of this variable in it's context.
     */
    @AntAccess
    public void setRole(@Nullable VariableRole role) {
        this.role = role;
    }

    /**
     * Returns the role of the variable and provides information about it's usage and purpose via the VariableRole enum.
     *
     * @return The role of this variable in it's context.
     */
    @AntAccess
    @Nullable
    public VariableRole getRole() {
        return role;
    }

}

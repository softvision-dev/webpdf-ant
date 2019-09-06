package net.webpdf.ant.task.variable;

import net.webpdf.ant.task.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores variables for a task and allows to retrieve said variables using their role.
 */
public class VariableMap {

    @Nullable
    private final Task task;
    @NotNull
    private final Map<VariableRole, Variable> variables = new HashMap<>();
    @NotNull
    private final List<Variable> unprocessedVariables = new ArrayList<>();

    /**
     * Creates a fresh variable container for the given task.F
     *
     * @param task The task a variable container shall be created for.
     */
    public VariableMap(@Nullable Task task) {
        this.task = task;
    }

    /**
     * Returns the variable, that is currently serving the given role. Or null, if such a variable can not be found.
     *
     * @param role The role the searched variable shall have.
     * @return The variable, that serves the given purpose, or null, if such a variable can not be found.
     */
    @Nullable
    public Variable getVar(@Nullable VariableRole role) {
        if (role == null) {
            return null;
        }
        processVariables();
        return variables.get(role);
    }

    /**
     * Add the given variable to this variable container.
     *
     * @param variable The variable, that shall be added to the task container.
     */
    public void add(@NotNull Variable variable) {
        unprocessedVariables.add(variable);
    }

    /**
     * Updates the map, using the collected, unprocessed variables.
     */
    private void processVariables() {
        if (unprocessedVariables.isEmpty()) {
            return;
        }
        for (Variable variable : unprocessedVariables) {
            if (variable != null && variable.getRole() != null) {
                variable.setOwningTask(task);
                variables.put(variable.getRole(), variable);
            }
        }

        unprocessedVariables.clear();
    }

    /**
     * Returns true if a variable has already been assigned to the given role.
     *
     * @param role The role, that shall be checked.
     * @return Returns true if a variable has been assigned to the given role.
     */
    public boolean isRoleTaken(@Nullable VariableRole role) {
        processVariables();
        return variables.containsKey(role);
    }

}

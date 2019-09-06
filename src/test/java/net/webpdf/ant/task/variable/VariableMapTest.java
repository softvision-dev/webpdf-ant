package net.webpdf.ant.task.variable;

import net.webpdf.ant.task.Task;
import net.webpdf.ant.task.TaskName;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class VariableMapTest {

    private Project owningProject;
    private Task owningTask;

    @Before
    public void prepare() {
        owningProject = new Project();
        owningTask = new Task(TaskName.OPERATION) {
            @Override
            public void execute() throws BuildException {
                //DO NOTHING
            }

            @Override
            public Project getProject() {
                return owningProject;
            }

            @Override
            public Location getLocation() {
                return Location.UNKNOWN_LOCATION;
            }

            @Override
            public Target getOwningTarget() {
                return new Target();
            }
        };
    }

    @Test
    public void testVariableMapping() {
        VariableMap variableMap = new VariableMap(owningTask);
        Variable out = new Variable();
        out.setName("out");
        out.setRole(VariableRole.OUTPUT);
        out.setValue("out1");
        Variable out2 = new Variable();
        out2.setName("out2");
        out2.setRole(VariableRole.OUTPUT);
        out2.setValue("out2");
        Variable in = new Variable();
        in.setName("in");
        in.setRole(VariableRole.INPUT);
        in.setValue("in");
        assertFalse(variableMap.isRoleTaken(VariableRole.OUTPUT));
        assertFalse(variableMap.isRoleTaken(VariableRole.INPUT));
        variableMap.add(out);
        variableMap.add(out2);
        variableMap.add(in);
        assertTrue(variableMap.isRoleTaken(VariableRole.OUTPUT));
        assertTrue(variableMap.isRoleTaken(VariableRole.INPUT));

        assertNull(variableMap.getVar(null));
        Variable output = variableMap.getVar(VariableRole.OUTPUT);
        assertNotNull(output);
        assertEquals("out2", output.getValue());
        Variable input = variableMap.getVar(VariableRole.INPUT);
        assertNotNull(input);
        assertEquals("in", input.getValue());
    }
}

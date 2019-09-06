package net.webpdf.ant.task.variable;

import net.webpdf.ant.task.Task;
import net.webpdf.ant.task.TaskName;
import org.apache.tools.ant.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class VariableTest {
    private Variable variable;
    private Project owningProject;

    @Before
    public void prepare() {
        variable = new Variable();
        owningProject = new Project();
        //DO NOTHING
        Task owningTask = new Task(TaskName.OPERATION) {
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

        variable.setOwningTask(owningTask);
    }

    @Test
    public void testPublishVariable() {
        variable.setName("name");
        variable.setRole(VariableRole.OUTPUT);
        variable.setValue("value");
        variable.execute();

        assertEquals("Variable should have been named.", "name", variable.getName());
        assertEquals("Variable should have been given a value.", "value", variable.getValue());
        assertEquals("Variable should have been assigned a role.", VariableRole.OUTPUT, variable.getRole());
        assertEquals("Variable should have been published to project.", "value", owningProject.getProperty("name"));
    }

}

package net.webpdf.ant.task.files;

import net.webpdf.ant.TestResources;
import net.webpdf.ant.task.Task;
import net.webpdf.ant.task.TaskName;
import net.webpdf.ant.task.variable.Variable;
import net.webpdf.ant.task.variable.VariableRole;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class IterativeTaskFileTest {

    private final TestResources testResources = new TestResources(IterativeTaskFileTest.class);
    private TempDir tempDir;

    @Before
    public void prepare() {
        tempDir = new TempDir();
    }

    @After
    public void cleanup() {
        tempDir.cleanTemp();
    }

    @Test(expected = BuildException.class)
    public void invalidNullSource() {
        new IterativeTaskFile(null, "donald-duck.pdf", tempDir);
    }

    @Test(expected = BuildException.class)
    public void invalidNullTarget() {
        new IterativeTaskFile(testResources.getResource("donald-duck.jpg"), null, tempDir);
    }

    @Test(expected = BuildException.class)
    public void invalidNullTempDir() {
        new IterativeTaskFile(testResources.getResource("donald-duck.jpg"), "donald-duck.pdf", null);
    }

    @Test
    public void tempFileCreation() throws Exception {
        File source = testResources.getResource("donald-duck.jpg");
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(source, "donald-duck.pdf", tempDir);
        File target = iterativeTaskFile.getCurrentTarget();
        assertTrue("The current target is not valid.",
            target.getAbsolutePath().startsWith(tempDir.getTempDir().getAbsolutePath()) &&
                target.exists() && target.isFile() && target.canRead() && target.canWrite() && !target.equals(source));
    }

    @Test
    public void tempFileIteration() throws Exception {
        File source = testResources.getResource("donald-duck.jpg");
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(source, "donald-duck.pdf", tempDir);
        iterativeTaskFile.prepareNextOperation();
        assertEquals("Iteration before first call to target should do nothing.", iterativeTaskFile.getCurrentSource(), source);
        File target = iterativeTaskFile.getCurrentTarget();
        assertTrue("The current target is not valid.",
            target.getAbsolutePath().startsWith(tempDir.getTempDir().getAbsolutePath()) &&
                target.exists() && target.isFile() && target.canRead() && target.canWrite() && !target.equals(source));
        iterativeTaskFile.prepareNextOperation();

        File target2 = iterativeTaskFile.getCurrentTarget();
        assertTrue("The current target is not valid.",
            target2.getAbsolutePath().startsWith(tempDir.getTempDir().getAbsolutePath()) &&
                target2.exists() && target2.isFile() && target2.canRead() && target2.canWrite() && !target2.equals(source)
                && !target.equals(target2));
        iterativeTaskFile.prepareNextOperation();
        assertFalse("First target should have been deleted now.", target.exists());
        assertEquals("Targets shall be used as sources of following steps.", target2, iterativeTaskFile.getCurrentSource());
        iterativeTaskFile.reset();
        assertEquals("OriginalSource should be regeneratable.", source, iterativeTaskFile.getCurrentSource());
    }

    @Test
    public void dropTarget() throws Exception {
        File source = testResources.getResource("donald-duck.jpg");
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(source, "donald-duck.pdf", tempDir);
        File target = iterativeTaskFile.getCurrentTarget();
        assertTrue("The current target is not valid.",
            target.getAbsolutePath().startsWith(tempDir.getTempDir().getAbsolutePath()) &&
                target.exists() && target.isFile() && target.canRead() && target.canWrite() && !target.equals(source));
        iterativeTaskFile.dropCurrentTarget();
        assertTrue("Target should have been dropped and deleted.",
            !target.exists() && !target.equals(iterativeTaskFile.getCurrentTarget()));
    }

    @Test
    public void finalizeAndReset() throws Exception {
        File source = testResources.getResource("donald-duck.jpg");
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(source, "donald-duck.pdf", tempDir);

        File targetFile = new File(tempDir.getTempDir(), "out.pdf");
        File previousTarget = iterativeTaskFile.getCurrentTarget();
        File resultFile = iterativeTaskFile.finalizeAndReset(targetFile, null);
        assertTrue("Result should have been created in place of given file.",
            targetFile.exists() && targetFile.isFile() && targetFile.canRead() &&
                targetFile.canWrite() && resultFile.equals(targetFile));
        assertTrue("Source and target should have been reset.",
            iterativeTaskFile.getCurrentSource().equals(source) && !iterativeTaskFile.getCurrentTarget().equals(previousTarget) &&
                !previousTarget.exists()
        );

        targetFile = new File(tempDir.getTempDir(), "donald-duck.pdf");
        previousTarget = iterativeTaskFile.getCurrentTarget();
        resultFile = iterativeTaskFile.finalizeAndReset(null, tempDir.getTempDir());
        assertTrue("Result should have been created in place of given file.",
            targetFile.exists() && targetFile.isFile() && targetFile.canRead() &&
                targetFile.canWrite() && resultFile.equals(targetFile));
        assertTrue("Source and target should have been reset.",
            iterativeTaskFile.getCurrentSource().equals(source) && !iterativeTaskFile.getCurrentTarget().equals(previousTarget) &&
                !previousTarget.exists()
        );

        assertEquals("The last processed file should have been returned.", iterativeTaskFile.getLastProcessed(), iterativeTaskFile.finalizeAndReset(null, null));
    }

    @Test
    public void testChangeSource() throws Exception {
        File source = testResources.getResource("donald-duck.jpg");
        File source2 = testResources.getResource("bmw_435.jpg");
        FileUtils.copyFileToDirectory(source2, tempDir.getTempDir());
        File copiedSource = new File(tempDir.getTempDir(), source2.getName());
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(source, "donald-duck.pdf", tempDir);

        iterativeTaskFile.setCurrentSource(copiedSource, false);
        iterativeTaskFile.getCurrentTarget();
        assertEquals("Source should have been altered.", iterativeTaskFile.getCurrentSource(), copiedSource);
        iterativeTaskFile.prepareNextOperation();
        assertFalse("Source should not have been preserved.", copiedSource.exists());

        FileUtils.copyFileToDirectory(source2, tempDir.getTempDir());
        iterativeTaskFile.setCurrentSource(copiedSource, true);
        iterativeTaskFile.getCurrentTarget();
        assertEquals("Source should have been altered.", iterativeTaskFile.getCurrentSource(), copiedSource);
        iterativeTaskFile.prepareNextOperation();
        assertTrue("Source should have been preserved.", copiedSource.exists());
        iterativeTaskFile.reset();
        assertEquals("Original source should have been restored.", source, iterativeTaskFile.getCurrentSource());
        assertTrue(copiedSource.delete());
    }

    @Test
    public void testPublish() throws Exception {
        File source = testResources.getResource("donald-duck.jpg");
        IterativeTaskFile iterativeTaskFile = new IterativeTaskFile(source, "donald-duck.pdf", tempDir);
        File currentTarget = iterativeTaskFile.getCurrentTarget();
        Variable var = new Variable();
        var.setName("name");
        var.setRole(VariableRole.OUTPUT);
        final Project proj = new Project();
        Task task = new Task(TaskName.OPERATION) {
            @Override
            public void execute() throws BuildException {
                //DO NOTHING
            }

            @Override
            public Project getProject() {
                return proj;
            }
        };
        var.setOwningTask(task);
        iterativeTaskFile.tryPublish(var);
        assertEquals("Value should have been published to project.", currentTarget.getAbsolutePath().replaceAll("\\\\", "/"), proj.getProperty("name"));
    }
}

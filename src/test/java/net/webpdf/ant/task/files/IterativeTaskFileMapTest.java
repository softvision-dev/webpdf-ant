package net.webpdf.ant.task.files;

import net.webpdf.ant.task.variable.Variable;
import net.webpdf.ant.task.variable.VariableRole;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.util.FileNameMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Iterator;

import static org.junit.Assert.*;

public class IterativeTaskFileMapTest {
    private net.webpdf.ant.task.files.IterativeTaskFileMap iterativeTaskFileMap;
    private FileNameMapper preparedMapper;

    @Before
    public void prepare() throws Exception {
        this.iterativeTaskFileMap = new IterativeTaskFileMap();
        preparedMapper = new FileNameMapper() {
            @Override
            public void setFrom(String from) {

            }

            @Override
            public void setTo(String to) {

            }

            @Override
            public String[] mapFileName(String sourceFileName) {
                return new String[]{sourceFileName + "mapped"};
            }
        };
    }

    @Test(expected = BuildException.class)
    public void testSetInvalidTempDir() {
        iterativeTaskFileMap.setTempDir(null);
    }

    @Test
    public void testSetTempDir() {
        File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
        iterativeTaskFileMap.setTempDir(tempDirectory);
        assertTrue("TempDir should have been initialized",
            iterativeTaskFileMap.getTempDir() != null && iterativeTaskFileMap.getTempDir().getTempDir().exists());
    }

    @Test(expected = BuildException.class)
    public void testSetInvalidTargetDir() {
        iterativeTaskFileMap.setTargetDirectory(null);
    }

    @Test
    public void testSetTargetDir() {
        File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
        iterativeTaskFileMap.setTargetDirectory(tempDirectory);
        assertTrue("TempDir should have been initialized",
            iterativeTaskFileMap.getTargetDirectory() != null && iterativeTaskFileMap.getTargetDirectory().exists());
    }

    @Test
    public void testSetTarget() throws Exception {
        net.webpdf.ant.task.files.TempDir tempDir = new net.webpdf.ant.task.files.TempDir();
        try {
            File target = tempDir.tryCreateTempFile();
            iterativeTaskFileMap.setTargetFile(target);
            assertEquals("TempDir should have been initialized", target, iterativeTaskFileMap.getTargetFile());
        } finally {
            tempDir.cleanTemp();
        }
    }

    @Test(expected = BuildException.class)
    public void testInvalidMapper() {
        iterativeTaskFileMap.setMapper(preparedMapper);
        iterativeTaskFileMap.setMapper(preparedMapper);
    }

    @Test
    public void testMapWithMapper() throws Exception {
        net.webpdf.ant.task.files.TempDir tempDir = new net.webpdf.ant.task.files.TempDir();
        File targetDir = new File(tempDir.getTempDir() + "/targetDir/");
        targetDir.mkdir();
        iterativeTaskFileMap.setMapper(preparedMapper);
        iterativeTaskFileMap.setTargetDirectory(tempDir.getTempDir());
        iterativeTaskFileMap.setTempDir(tempDir.getTempDir());
        try {
            Project project = new Project();
            File file = tempDir.tryCreateTempFile();
            iterativeTaskFileMap.add(new FileResource(project, file));
            Iterator<IterativeTaskFile> it = iterativeTaskFileMap.iterator();
            IterativeTaskFile iterativeTaskFile = it.next();
            File target = iterativeTaskFile.finalizeAndReset(null, targetDir);
            assertEquals("Unexpected target name.", file.getName() + "mapped", target.getName());
        } finally {
            tempDir.cleanTemp();
            targetDir.delete();
        }
    }

    @Test
    public void testMapWithoutMapper() throws Exception {
        TempDir tempDir = new TempDir();
        File targetDir = new File(tempDir.getTempDir() + "/targetDir/");
        targetDir.mkdir();
        iterativeTaskFileMap.setTargetDirectory(targetDir);
        iterativeTaskFileMap.setTempDir(tempDir.getTempDir());
        try {
            Project project = new Project();
            File file = tempDir.tryCreateTempFile();
            iterativeTaskFileMap.add(new FileResource(project, file));
            Iterator<IterativeTaskFile> it = iterativeTaskFileMap.iterator();
            IterativeTaskFile iterativeTaskFile = it.next();
            File target = iterativeTaskFile.finalizeAndReset(null, targetDir);
            assertEquals("Unexpected target name.", file.getName(), target.getName());
        } finally {
            tempDir.cleanTemp();
            targetDir.delete();
        }
    }

    @Test
    public void testMapDirectory() throws Exception {
        TempDir tempDir = new TempDir();
        File sourceDir = new File(tempDir.getTempDir() + "/sourceDir/");
        sourceDir.mkdir();
        File sourceFile = new File(sourceDir, "/sourceFile.temp");
        sourceFile.createNewFile();
        File targetDir = tempDir.getTempDir();
        targetDir.mkdir();
        iterativeTaskFileMap.setMapper(preparedMapper);
        iterativeTaskFileMap.setTargetDirectory(targetDir);
        iterativeTaskFileMap.setTempDir(tempDir.getTempDir());
        try {
            Project project = new Project();
            iterativeTaskFileMap.add(new FileResource(project, sourceDir));
            Iterator<IterativeTaskFile> it = iterativeTaskFileMap.iterator();
            IterativeTaskFile iterativeTaskFile = it.next();
            File target = iterativeTaskFile.finalizeAndReset(null, targetDir);
            assertEquals("Unexpected target name.", sourceFile.getName() + "mapped", target.getName());
        } finally {
            tempDir.cleanTemp();
            targetDir.delete();
            sourceDir.delete();
        }
    }

    @Test
    public void testMapInexistentResource() throws Exception {
        TempDir tempDir = new TempDir();
        iterativeTaskFileMap.setTempDir(tempDir.getTempDir());
        try {
            File file = tempDir.tryCreateTempFile();
            iterativeTaskFileMap.add(new FileResource(file, "name"));
            Iterator<IterativeTaskFile> it = iterativeTaskFileMap.iterator();
            assertFalse("Resource should not have been added, as it is not linked to a project.", it.hasNext());
        } finally {
            tempDir.cleanTemp();
        }
    }

    @Test
    public void testMapFileRessource() throws Exception {
        TempDir tempDir = new TempDir();
        File sourceDir = new File(tempDir.getTempDir() + "/sourceDir/");
        sourceDir.mkdir();
        File sourceFile = new File(sourceDir, "/sourceFile.temp");
        sourceFile.createNewFile();
        File targetDir = new File(tempDir.getTempDir() + "/targetDir/");
        targetDir.mkdir();
        iterativeTaskFileMap.setMapper(preparedMapper);
        iterativeTaskFileMap.setTargetDirectory(tempDir.getTempDir());
        iterativeTaskFileMap.setTempDir(tempDir.getTempDir());
        try {
            Project project = new Project();
            iterativeTaskFileMap.add(new FileResource(project, sourceFile));
            Iterator<IterativeTaskFile> it = iterativeTaskFileMap.iterator();
            IterativeTaskFile iterativeTaskFile = it.next();
            File target = iterativeTaskFile.finalizeAndReset(null, targetDir);
            assertEquals("Unexpected target name.", sourceFile.getName() + "mapped", target.getName());
        } finally {
            tempDir.cleanTemp();
            targetDir.delete();
            sourceDir.delete();
        }
    }

    @Test
    public void testMapFileSet() throws Exception {
        TempDir tempDir = new TempDir();
        File sourceDir = new File(tempDir.getTempDir() + "/sourceDir/");
        sourceDir.mkdir();
        File sourceFile = new File(sourceDir, "/sourceFile.temp");
        sourceFile.createNewFile();
        File targetDir = new File(tempDir.getTempDir() + "/targetDir/");
        targetDir.mkdir();
        iterativeTaskFileMap.setMapper(preparedMapper);
        iterativeTaskFileMap.setTargetDirectory(tempDir.getTempDir());
        iterativeTaskFileMap.setTempDir(tempDir.getTempDir());

        try {
            Project project = new Project();
            DirectoryScanner directoryScanner = new DirectoryScanner();
            directoryScanner.setBasedir(sourceDir);
            directoryScanner.setIncludes(new String[]{"**/*.temp"});
            FileSet fileSet = new FileSet();
            fileSet.setProject(project);
            fileSet.setDir(sourceDir);
            fileSet.setupDirectoryScanner(directoryScanner);
            iterativeTaskFileMap.add(fileSet);
            Iterator<IterativeTaskFile> it = iterativeTaskFileMap.iterator();
            IterativeTaskFile iterativeTaskFile = it.next();
            File target = iterativeTaskFile.finalizeAndReset(null, targetDir);
            assertEquals("Unexpected target name.", sourceFile.getName() + "mapped", target.getName());
        } finally {
            tempDir.cleanTemp();
            targetDir.delete();
            sourceDir.delete();
        }
    }

    @Test
    public void testOtherResourceCollection() throws Exception {
        TempDir tempDir = new TempDir();
        File sourceDir = new File(tempDir.getTempDir() + "/sourceDir/");
        sourceDir.mkdir();
        File sourceFile = new File(sourceDir, "/sourceFile.temp");
        sourceFile.createNewFile();
        File targetDir = new File(tempDir.getTempDir() + "/targetDir/");
        targetDir.mkdir();
        iterativeTaskFileMap.setMapper(preparedMapper);
        iterativeTaskFileMap.setTargetDirectory(tempDir.getTempDir());
        iterativeTaskFileMap.setTempDir(tempDir.getTempDir());

        try {
            Project project = new Project();
            Resources resources = new Resources(project);
            resources.add(new FileResource(project, sourceFile));
            iterativeTaskFileMap.add(resources);
            Iterator<IterativeTaskFile> it = iterativeTaskFileMap.iterator();
            IterativeTaskFile iterativeTaskFile = it.next();
            File target = iterativeTaskFile.finalizeAndReset(null, targetDir);
            assertEquals("Unexpected target name.", sourceFile.getName() + "mapped", target.getName());
        } finally {
            tempDir.cleanTemp();
            targetDir.delete();
            sourceDir.delete();
        }
    }

    @Test
    public void testNonExistentResource() throws Exception {
        TempDir tempDir = new TempDir();
        File sourceDir = new File(tempDir.getTempDir() + "/sourceDir/");
        sourceDir.mkdir();
        File sourceFile = new File(sourceDir, "/sourceFile.temp");
        sourceFile.createNewFile();
        File targetDir = new File(tempDir.getTempDir() + "/targetDir/");
        targetDir.mkdir();
        iterativeTaskFileMap.setMapper(preparedMapper);
        iterativeTaskFileMap.setTargetDirectory(tempDir.getTempDir());
        iterativeTaskFileMap.setTempDir(tempDir.getTempDir());

        try {
            Project project = new Project();
            Resources resources = new Resources(project);
            resources.add(new FileResource(project, new File("NonExistentFile.non")));
            iterativeTaskFileMap.add(resources);
            Iterator<IterativeTaskFile> it = iterativeTaskFileMap.iterator();
            assertFalse("An invalid resource should never be added to the map.", it.hasNext());
        } finally {
            tempDir.cleanTemp();
            targetDir.delete();
            sourceDir.delete();
        }
    }

    @Test
    public void testUseSourceVar() throws Exception {
        TempDir tempDir = new TempDir();
        File sourceDir = new File(tempDir.getTempDir() + "/sourceDir/");
        sourceDir.mkdir();
        File sourceFile = new File(sourceDir, "/sourceFile.temp");
        sourceFile.createNewFile();
        File targetDir = new File(tempDir.getTempDir() + "/targetDir/");
        targetDir.mkdir();
        iterativeTaskFileMap.setMapper(preparedMapper);
        iterativeTaskFileMap.setTargetDirectory(tempDir.getTempDir());
        iterativeTaskFileMap.setTempDir(tempDir.getTempDir());

        try {
            Project project = new Project();
            Variable sourceVar = new Variable();
            sourceVar.setName("source");
            sourceVar.setValue(sourceFile.getAbsolutePath());
            sourceVar.setRole(VariableRole.INPUT);
            iterativeTaskFileMap.replaceWithInputVar(sourceVar);
            Iterator<IterativeTaskFile> it = iterativeTaskFileMap.iterator();
            IterativeTaskFile iterativeTaskFile = it.next();
            File target = iterativeTaskFile.finalizeAndReset(null, targetDir);
            assertEquals("Unexpected target name.", sourceFile.getName() + "mapped", target.getName());
        } finally {
            tempDir.cleanTemp();
            targetDir.delete();
            sourceDir.delete();
        }
    }

    @Test
    public void testUseTargetVar() throws Exception {
        TempDir tempDir = new TempDir();
        File sourceDir = new File(tempDir.getTempDir() + "/sourceDir/");
        sourceDir.mkdir();
        File sourceFile = new File(sourceDir, "/sourceFile.temp");
        sourceFile.createNewFile();
        File targetDir = new File(tempDir.getTempDir() + "/targetDir/");
        targetDir.mkdir();
        iterativeTaskFileMap.setMapper(preparedMapper);
        iterativeTaskFileMap.setTargetDirectory(tempDir.getTempDir());
        iterativeTaskFileMap.setTempDir(tempDir.getTempDir());

        try {
            Project project = new Project();
            Variable sourceVar = new Variable();
            sourceVar.setName("source");
            sourceVar.setValue(sourceFile.getAbsolutePath());
            sourceVar.setRole(VariableRole.OUTPUT);
            iterativeTaskFileMap.replaceWithInputVar(sourceVar);
            Iterator<IterativeTaskFile> it = iterativeTaskFileMap.iterator();
            assertFalse("Output variables should not be added to the map.", it.hasNext());
        } finally {
            tempDir.cleanTemp();
            targetDir.delete();
            sourceDir.delete();
        }
    }

    @Test
    public void testUseNonExistentSourceVar() throws Exception {
        TempDir tempDir = new TempDir();
        File sourceDir = new File(tempDir.getTempDir() + "/sourceDir/");
        sourceDir.mkdir();
        File sourceFile = new File(sourceDir, "/sourceFile.temp");
        sourceFile.createNewFile();
        File targetDir = new File(tempDir.getTempDir() + "/targetDir/");
        targetDir.mkdir();
        iterativeTaskFileMap.setMapper(preparedMapper);
        iterativeTaskFileMap.setTargetDirectory(tempDir.getTempDir());
        iterativeTaskFileMap.setTempDir(tempDir.getTempDir());

        try {
            Project project = new Project();
            Variable sourceVar = new Variable();
            sourceVar.setName("source");
            sourceVar.setValue("NonExistentFile.non");
            sourceVar.setRole(VariableRole.INPUT);
            iterativeTaskFileMap.replaceWithInputVar(sourceVar);
            Iterator<IterativeTaskFile> it = iterativeTaskFileMap.iterator();
            assertFalse("Non existent files should never be added to the map, even when coming from a source var.", it.hasNext());
        } finally {
            tempDir.cleanTemp();
            targetDir.delete();
            sourceDir.delete();
        }
    }
}

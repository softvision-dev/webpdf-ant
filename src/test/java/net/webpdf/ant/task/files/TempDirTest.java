package net.webpdf.ant.task.files;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class TempDirTest {
    @Test
    public void createTempDirFromDefault() throws Exception {
        TempDir tempDir = new TempDir();
        assertEquals("Temp directory should have been set to system default temp dir.", new File(System.getProperty("java.io.tmpdir")), tempDir.getTempDir());
    }

    @Test
    public void createTempDirFromFile() throws Exception {
        File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
        TempDir tempDir = new TempDir(tempDirectory);
        assertEquals("Temp directory should have been set to system default temp dir.", tempDirectory, tempDir.getTempDir());
    }

    @Test
    public void createTempDirFromNull() throws Exception {
        TempDir tempDir = new TempDir(null);
        assertEquals("Temp directory should have been set to system default temp dir.", new File(System.getProperty("java.io.tmpdir")), tempDir.getTempDir());
    }

    @Test
    public void createTempFileDefault() throws Exception {
        TempDir tempDir = new TempDir();
        File tempFile = tempDir.tryCreateTempFile();
        assertTrue("Temp file should have been created.", tempFile != null && tempFile.isFile() && tempFile.exists());
        tempDir.cleanTemp();
        assertFalse("Temp file should have been deleted.", tempFile != null && tempFile.isFile() && tempFile.exists());
    }

    @Test
    public void createTempFileSet() throws Exception {
        File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
        TempDir tempDir = new TempDir(tempDirectory);
        File tempFile = tempDir.tryCreateTempFile();
        assertTrue("Temp file should have been created.", tempFile != null && tempFile.isFile() && tempFile.exists());
        tempDir.cleanTemp();
        assertFalse("Temp file should have been deleted.", tempFile != null && tempFile.isFile() && tempFile.exists());
    }
}

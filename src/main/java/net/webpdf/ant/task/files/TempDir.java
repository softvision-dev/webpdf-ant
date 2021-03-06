package net.webpdf.ant.task.files;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This points to the temporary directory used by the task and will collect all temporary files created during the webPDF call.
 */
public class TempDir {

    private static final String NEUTRAL_SUFFIX = ".temp";
    private static final String WEBPDF_ANT_PREFIX = "webPDF-ant_";

    @NotNull
    private final List<File> createdTempFiles = new ArrayList<>();
    @Nullable
    private File tempDir;

    /**
     * A directory serving as the temporary directory of the webPDF task.
     *
     * @param tempDir The directory serving as the temporary directory of the webPDF task.
     */
    TempDir(@Nullable File tempDir) {
        this.tempDir = tempDir;
    }

    /**
     * A directory serving as the temporary directory of the webPDF task. (using the systems default temp dir)
     */
    public TempDir() {
        this(null);
    }

    /**
     * Creates a temporary file in either the given temporary directory, or the systems default temporary directory.
     * The filename shall follow this naming convention: webPDF-ant_{UniqueID}.temp
     *
     * @return The temporary file, that has been created.
     * @throws IOException Shall be thrown, if the temporary file could not be created.
     */
    @NotNull
    File tryCreateTempFile() throws IOException {
        File tempFile;
        if (tempDir != null && tempDir.exists() && tempDir.isDirectory() && tempDir.canWrite()) {
            tempFile = File.createTempFile(WEBPDF_ANT_PREFIX, NEUTRAL_SUFFIX, tempDir);
        } else {
            tempFile = File.createTempFile(WEBPDF_ANT_PREFIX, NEUTRAL_SUFFIX);
        }
        createdTempFiles.add(tempFile);
        return tempFile;
    }

    /**
     * All temporary files, having been created by this instance and not having been deleted yet, will be deleted by calling this method.
     */
    public void cleanTemp() {
        for (File tempFile : createdTempFiles) {
            FileUtils.deleteQuietly(tempFile);
        }
    }

    /**
     * Returns the actual temp directory.
     *
     * @return The actural temp directory.
     */
    @NotNull
    public File getTempDir() {
        if (tempDir == null) {
            return new File(System.getProperty("java.io.tmpdir"));
        }
        return tempDir;
    }

}

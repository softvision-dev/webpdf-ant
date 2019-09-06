package net.webpdf.ant.task.files;

import net.webpdf.ant.task.variable.Variable;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * This class manages the files processed by a task, it shall start with the original source file and shall provide a
 * temporary target file for each calling sub task. Using this class to manage files enables tasks to manage their
 * sources and targets in a common and centralized way. This class will attempt to prepare the result of a previously
 * calling task as the source of the following task. The original source file will never be altered using an instance
 * of this class. This class will never delete the original source file. This class provides a mechanism to copy the
 * terminal temporary file to the intended target path at the end.
 */
public class IterativeTaskFile {

    @NotNull
    private final TempDir tempDir;
    @NotNull
    private final File originalSourceFile;
    @NotNull
    private final String targetFileName;

    @NotNull
    private File currentSource;
    @Nullable
    private File currentTarget;

    private boolean preserveCurrentSource = true;

    /**
     * An instance of this class manages files for encapsulated tasks - passing results of a previous task, as the source
     * for following task. It shall automatically create temporary files in between the calling tasks, it shall prevent
     * the original source file from being modified or deleted. It shall provide a mechanism to copy the end result to a
     * specified target path.
     *
     * @param originalSourceFile The initial source file, that shall be processed iteratively.
     * @param targetFileName     The name of the terminal target file.
     * @param tempDir            The directory temporary files shall be placed in.
     * @throws BuildException Shall be thrown if any of the parameters is invalid (null).
     */
    public IterativeTaskFile(@Nullable File originalSourceFile, @Nullable String targetFileName, @Nullable TempDir tempDir) throws BuildException {
        if (originalSourceFile == null) {
            throw new BuildException("Incomplete source definition: source File has not been set.");
        }
        if (targetFileName == null) {
            throw new BuildException("Incomplete source definition: source File has not been set for: %s" + originalSourceFile.getAbsolutePath());
        }
        if (tempDir == null) {
            throw new BuildException("Incomplete source definition: temporary directory has not been set for: %s" + originalSourceFile.getAbsolutePath());
        }
        this.tempDir = tempDir;
        this.originalSourceFile = originalSourceFile;
        this.currentSource = this.originalSourceFile;
        this.targetFileName = targetFileName;
    }

    /**
     * Shall set the file, that is serving as the source file of the currently prepared step.
     *
     * @param currentSource  The file, that shall be used as the source  of the currently prepared step.
     * @param preserveSource When set to true the prepared source file shall not be deleted after the execution.
     */
    public void setCurrentSource(@NotNull File currentSource, boolean preserveSource) {
        this.currentSource = currentSource;
        this.preserveCurrentSource = preserveSource;
    }

    /**
     * Shall return the file, that is serving as the source file of the currently prepared step.
     *
     * @return The current source file.
     */
    @NotNull
    public File getCurrentSource() {
        return currentSource;
    }

    /**
     * When called the current temporary source file will not be deleted, when it is replaced by the current target in
     * next step. This method must be called for each individual step and is only active for the current step.
     */
    public void preserveCurrentSource() {
        preserveCurrentSource = true;
    }

    /**
     * This shall return the current target file - if the current target file has not yet been initialized, a new
     * temporary target file shall be prepared by calling this method.
     *
     * @return The current temporary target file (the source file of the next step).
     * @throws IOException Shall be thrown if a temporary target file could not be created for some reason.
     */
    @NotNull
    public File getCurrentTarget() throws IOException {
        if (currentTarget == null) {
            currentTarget = tempDir.tryCreateTempFile();
        }
        return currentTarget;
    }

    /**
     * This shall decide whether the current temporary source file shall be deleted or not. (the original source file
     * however will never be deleted using this method.) It shall replace said source file with the current target file.
     * This leads to the following behaviour: the result of the previous operation serves as the source of the next.
     * The deletion of the source file can be prevented entirely by calling the "preserveCurrentSource()" method.
     */
    public void prepareNextOperation() {
        if (currentTarget == null) {
            return;
        }
        if (!preserveCurrentSource) {
            tryDelete(currentSource);
        }
        preserveCurrentSource = false;
        this.currentSource = currentTarget;
        this.currentTarget = null;
    }

    /**
     * This shall both reset this instance to it's original state and shall copy the last produced result to either the
     * given targetFile (using the exact name and path), or to the given targetDirectory (using the initially set targetName).
     *
     * @param targetFile      The file the final result shall be copied to.
     * @param targetDirectory The directory the target file shall be created in.
     * @return The file the result has been published to.
     * @throws IOException Shall be thrown if the target file could not be copied/created.
     */
    @NotNull
    public File finalizeAndReset(@Nullable File targetFile, @Nullable File targetDirectory) throws IOException {
        try {
            File lastProcessed = getLastProcessed();

            if (targetFile != null && (!targetFile.exists() || targetFile.delete()) && targetFile.getParentFile().exists() && targetFile.getParentFile().canWrite()) {
                FileUtils.copyFile(lastProcessed, targetFile);
                return targetFile;
            } else if (targetDirectory != null && targetDirectory.exists() && targetDirectory.isDirectory() && targetDirectory.canWrite()) {
                File result = new File(targetDirectory, targetFileName);
                FileUtils.copyFile(lastProcessed, result);
                return result;
            }

            return lastProcessed;
        } finally {
            reset();
        }
    }

    /**
     * This shall return the last file, that has been altered by this instance. Choosing source or target file appropriately.
     *
     * @return The last processed file.
     */
    @NotNull
    File getLastProcessed() {
        return currentTarget != null ? currentTarget : currentSource;
    }

    /**
     * This will drop the current context and will reset this instance to the original source document.
     */
    public void reset() {
        if (!preserveCurrentSource) {
            tryDelete(currentSource);
        }
        tryDelete(currentTarget);
        currentTarget = null;
        currentSource = originalSourceFile;
        preserveCurrentSource();
    }

    /**
     * Invalidates the current temporary target and will preserve the current source file for the next operation.
     * This method should be called, when an Exception occurred during the processing of an operation.
     */
    public void dropCurrentTarget() {
        if (this.currentTarget != null) {
            tryDelete(currentTarget);
        }
        this.currentTarget = null;
    }

    /**
     * Attempts to delete the given file, returning true when successful.
     *
     * @param file The file that shall be deleted.
     */
    private void tryDelete(@Nullable File file) {
        if (!originalSourceFile.equals(file) && file != null && file.exists() && file.isFile() && file.canWrite()) {
            FileUtils.deleteQuietly(file);
        }
    }

    /**
     * Attempts to publish the current group result to the Ant context using the given output var name.
     */
    public void tryPublish(@Nullable Variable variable) {
        if (variable != null && currentTarget != null && currentTarget.exists() && currentTarget.isFile() && currentTarget.canRead()) {
            variable.setValue(currentTarget.getAbsolutePath().replaceAll("\\\\", "/"));
            variable.execute();
            currentTarget = null;
        }
    }

}

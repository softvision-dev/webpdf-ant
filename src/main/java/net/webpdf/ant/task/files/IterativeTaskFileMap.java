package net.webpdf.ant.task.files;

import net.webpdf.ant.task.variable.Variable;
import net.webpdf.ant.task.variable.VariableRole;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.ant.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The TaskFileCollection collects all files, that the current webPDF task shall be executed for.
 * An instance of this class will also provide information about the used and needed directories.
 * It also provides a mapping from each individual source file to the intended target file name.
 */
public class IterativeTaskFileMap implements Iterable<IterativeTaskFile> {

    private final List<ResourceCollection> resourceCollections = new ArrayList<>();

    private List<IterativeTaskFile> taskFileList = new ArrayList<>();
    private FileNameMapper fileNameMapper = null;
    private TempDir tempDir;
    private File targetDirectory;
    private File targetFile;

    private boolean resourceInitialized = false;

    /**
     * A collection for all source files, that shall be processed by the current webPDF task.
     */
    public IterativeTaskFileMap() {
    }

    /**
     * Sets the directory temporary files shall be created in.
     *
     * @param tempDir The directory temporary files shall be created in.
     * @throws BuildException Shall be thrown if the given directory is invalid or inaccessible.
     */
    public void setTempDir(File tempDir) throws BuildException {
        if (tempDir == null || !tempDir.exists() || !tempDir.canWrite() || !tempDir.isDirectory()) {
            throw new BuildException("The given temporary directory is inaccessible.");
        }
        this.tempDir = new TempDir(tempDir);
    }

    public TempDir getTempDir() {
        if (this.tempDir == null) {
            this.tempDir = new TempDir();
        }
        return tempDir;
    }

    /**
     * Sets the directory the final result shall be placed in.
     *
     * @param targetDirectory The directory the final result shall be placed in.
     * @throws BuildException Shall be thrown if the given directory is invalid or inaccessible.
     */
    public void setTargetDirectory(File targetDirectory) throws BuildException {
        if (targetDirectory == null || (!targetDirectory.exists() && !targetDirectory.mkdirs()) || !targetDirectory.isDirectory() || !targetDirectory.canWrite()) {
            throw new BuildException("The given target directory is inaccessible.");
        }

        this.targetDirectory = targetDirectory;
    }

    /**
     * Shall return the directory the final result shall be placed in.
     *
     * @return The directory the final result shall be placed in.
     */
    public File getTargetDirectory() {
        return targetDirectory;
    }

    /**
     * Sets the explicit file the final result shall be copied to.
     *
     * @param targetFile The file the final result shall be copied to.
     */
    public void setTargetFile(File targetFile) {
        this.targetFile = targetFile;
    }

    /**
     * Returns the file the final result shall be copied to.
     *
     * @return The file the final result shall be copied to.
     */
    public File getTargetFile() {
        return targetFile;
    }

    /**
     * Returns a mapper, that is used to deduce the target file name from the source file name.
     *
     * @return The mapper used to deduce the target file name from the source file name.
     */
    private FileNameMapper getMapper() {
        if (fileNameMapper == null) {
            fileNameMapper = new IdentityMapper();
        }
        return fileNameMapper;
    }

    /**
     * Shall analyze all Resources, that have been collected up to now and will deduce both a source file and a mapped
     * target name for each resource.
     */
    private void parseResources() {
        for (ResourceCollection collection : resourceCollections) {
            if (collection instanceof FileResource) {
                FileResource fileResource = (FileResource) collection;
                if (!checkResourceExists(fileResource)) {
                    continue;
                }
                mapFile(fileResource.getFile());
                continue;
            }
            if (collection instanceof FileSet) {
                FileSet files = (FileSet) collection;
                DirectoryScanner directoryScanner = files.getDirectoryScanner();
                for (String fileName : directoryScanner.getIncludedFiles()) {
                    mapFile(new File(directoryScanner.getBasedir(), fileName));
                }
                continue;
            }

            for (Resource resource : collection) {
                if (!checkResourceExists(resource)) {
                    continue;
                }
                final FileProvider fileProvider = resource.as(FileProvider.class);
                if (fileProvider != null) {
                    FileResource fileResource = ResourceUtils.asFileResource(fileProvider);
                    mapFile(fileResource.getFile());
                }
            }
        }
    }

    /**
     * This will test whether a given resource references an accessible and readable file or not.
     *
     * @param resource The resource, that shall be checked.
     * @return True, if a matching file could be found, that is defined by the given resource.
     */
    private boolean checkResourceExists(Resource resource) {
        return resource.isFilesystemOnly() && resource.isExists();
    }

    /**
     * Will add a mapping for the given file to this collection.
     *
     * @param file The file, that shall serve as a source of the current webPDF task.
     */
    private void mapFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    mapFile(subFile);
                }
            }
        } else {
            mapFile(file, file.getName());
        }
    }

    /**
     * Deduces a target file name for the given source file and creates a matching entry in the task file map.
     *
     * @param file           The source file, that shall be mapped.
     * @param mappedFileName The name that shall be mapped.
     */
    private void mapFile(File file, String mappedFileName) {
        String[] filenames = getMapper().mapFileName(mappedFileName);
        if (filenames != null && filenames.length >= 1) {
            taskFileList.add(new IterativeTaskFile(file, filenames[0], getTempDir()));
        }
    }

    /**
     * Will add each resource defined in the given collection.
     *
     * @param resourceCollection The collection, that shall be appended to this task file collection.
     */
    public void add(ResourceCollection resourceCollection) {
        resourceCollections.add(resourceCollection);
    }

    /**
     * Initializes the mapper, that will be used to deduce target file names for source files.
     *
     * @param fileNameMapper The mapper, that will be used to deduce target file names for source files.
     * @throws BuildException Shall be thrown if the mapper is not valid, or if a valid mapper has already been set.
     */
    public void setMapper(FileNameMapper fileNameMapper) throws BuildException {
        if (this.fileNameMapper != null && !(this.fileNameMapper instanceof IdentityMapper)) {
            throw new BuildException("Only one mapper may be set per task.");
        }
        this.fileNameMapper = fileNameMapper;
    }

    /**
     * Overrides the currently set resources with a manually set input variable.
     *
     * @param input The input variable, that shall replace the currently set resources.
     * @throws IOException Shall be thrown, if the given file can not be accessed.
     */
    public void replaceWithInputVar(Variable input) throws IOException {
        File file;
        if (input != null && input.getValue() != null && VariableRole.INPUT.equals(input.getRole()) && (file = new File(input.getValue())).exists() && file.isFile() && file.canRead()) {
            File tempFile = getTempDir().tryCreateTempFile();
            FileUtils.copyFile(file, tempFile);
            taskFileList.clear();
            resourceCollections.clear();
            mapFile(tempFile, file.getName());
        }
    }

    /**
     * An iterator over all source/name mappings of this collection.
     *
     * @return An iterator for this collection.
     */
    @Override
    public Iterator<IterativeTaskFile> iterator() {
        if (!resourceInitialized) {
            parseResources();
            resourceInitialized = true;
        }
        return taskFileList.iterator();
    }
}

package net.webpdf.ant;

import net.webpdf.ant.task.AntAccess;
import net.webpdf.ant.task.Task;
import net.webpdf.ant.task.TaskConfiguration;
import net.webpdf.ant.task.TaskName;
import net.webpdf.ant.task.credentials.NTCredentials;
import net.webpdf.ant.task.credentials.UserCredentials;
import net.webpdf.ant.task.files.IterativeTaskFile;
import net.webpdf.ant.task.logging.LogTag;
import net.webpdf.ant.task.variable.Variable;
import net.webpdf.ant.task.variable.VariableRole;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.FileNameMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This task is representing the top level of the webPDF ANT task. It shall contain all basic configurations necessary to
 * connect to the webPDF server and shall share those connection details with all contained sub tasks.
 */
public class WebPDFTask extends Task implements TaskContainer {
    private final List<org.apache.tools.ant.Task> tasks = new ArrayList<>();

    /**
     * An instance of this class is representing the top level of the webPDF ANT task. It shall contain all basic
     * configurations necessary to connect to the webPDF server and shall share those connection details with all
     * contained sub tasks. It must be contained and is providing the basic context for all webservice calls.
     * <p>
     * It may contain either grouped operations <group> or operation definitions <operation> themselves.
     */
    @AntAccess
    public WebPDFTask() {
        super(TaskName.WEBPDF);
        setTaskConfiguration(new TaskConfiguration());
    }

    /**
     * Central method to execute a task and all it's sub tasks. This method shall prepare and provide the needed context
     * and shall execute it's sub tasks sequentially in an ordered fashion.
     */
    @Override
    public void execute() throws BuildException {
        if (getVariables().isRoleTaken(VariableRole.INPUT)) {
            try {
                getTaskConfiguration().getTaskFiles().replaceWithInputVar(getVariables().getVar(VariableRole.INPUT));
            } catch (IOException ex) {
                throw new BuildException(ex.getMessage(), ex, getLocation());
            }
        }
        try {
            for (IterativeTaskFile taskFile : getTaskConfiguration().getTaskFiles()) {
                getLogger().info(LogTag.WEBPDF_TASK);
                for (org.apache.tools.ant.Task task : tasks) {
                    processTask(task, taskFile);
                }
                try {
                    File target = taskFile.finalizeAndReset(
                        getTaskConfiguration().getTaskFiles().getTargetFile(),
                        getTaskConfiguration().getTaskFiles().getTargetDirectory()
                    );
                    if (getVariables().isRoleTaken(VariableRole.OUTPUT)) {
                        Variable targetVar = getVariables().getVar(VariableRole.OUTPUT);
                        targetVar.setValue(target.getAbsolutePath().replaceAll("\\\\", "/"));
                        targetVar.execute();
                    }
                    getLogger().info("Write to " + (target != null ? target.getAbsolutePath() : "FILE NAME UNKNOWN"), LogTag.TARGET);
                } catch (IOException ex) {
                    String message = "Creation of the final target file failed.";
                    getLogger().error(message, ex, LogTag.WEBPDF_TASK);
                    throw new BuildException(message, ex, getLocation());
                }
            }
        } finally {
            getTaskConfiguration().getTaskFiles().getTempDir().cleanTemp();
        }
    }

    /**
     * Process a single sub task.
     */
    private void processTask(org.apache.tools.ant.Task task, IterativeTaskFile taskFile) throws BuildException {
        if (task instanceof GroupTask) {
            GroupTask group = (GroupTask) task;
            try {
                group.setFiles(taskFile);
                task.execute();
            } catch (BuildException ex) {
                if (getTaskConfiguration().isFailOnError()) {
                    taskFile.reset();
                    getLogger().error(ex, LogTag.WEBPDF_TASK);
                    throw ex;
                } else {
                    getLogger().warn(ex, LogTag.WEBPDF_TASK);
                }
            }
        } else {
            task.reconfigure();
            task.execute();
        }
    }

    /**
     * Method to add sub elements to this tag. This shall be used by ant access methods and shall treat each appended
     * element according to it's type.
     *
     * @param task The sub task, that shall be added.
     */
    @Override
    @AntAccess
    public void addTask(org.apache.tools.ant.Task task) {
        if (task instanceof OperationTask) {
            if (getTaskConfiguration().isFailOnError()) {
                throw new BuildException("A webPDF task may not contain operations directly.");
            }
            getLogger().warn("A webPDF task may not contain operations directly.", LogTag.WEBPDF_TASK);
            return;
        }

        tasks.add(task);
    }

    /**
     * This shall add the given items as sources of this task. The defined operations shall be executed for each
     * of those given sources.
     * <p>
     * It is not expected to use this method actively, as it is specifically defined for the access by ANT.
     *
     * @param resourceCollection A collection of resources, that shall be processed by this task.
     */
    @AntAccess
    public void add(ResourceCollection resourceCollection) {
        getTaskConfiguration().getTaskFiles().add(resourceCollection);
    }

    /**
     * This shall configure the mapper used by this task, to determine the name of the generated target file.
     * The resulting target name shall be chosen according to the rules defined in the mapper and depending on the name
     * of the source file.
     * <p>
     * It is not expected to use this method actively, as it is specifically defined for the access by ANT.
     *
     * @param fileNameMapper The mapper defining naming conventions for target files of this task.
     */
    @AntAccess
    public void add(FileNameMapper fileNameMapper) {
        getTaskConfiguration().getTaskFiles().setMapper(fileNameMapper);
    }

    /**
     * This method shall add a bundled <group> task to this task. The group shall contain, define and configure a
     * sequence of operations.
     * <p>
     * It is not expected to use this method actively, as it is specifically defined for the access by ANT.
     *
     * @param task A group task containing a sequence of webservice operations.
     */
    @AntAccess
    public void add(GroupTask task) {
        task.setTaskConfiguration(getTaskConfiguration());
        tasks.add(task);
    }

    /**
     * Provides basic user credentials (username and password) for the authentication at the webPDF server.
     *
     * @param credentials The credentials task containing all necessary authentication information.
     */
    @AntAccess
    public void add(UserCredentials credentials) {
        getTaskConfiguration().setCredentialsTask(credentials);
    }

    /**
     * Provides credentials for windows platforms, containing username and password and optionally the name of the user's
     * workstation and domain name.
     *
     * @param credentials The credentials task containing all necessary authentication information.
     */
    @AntAccess
    public void add(NTCredentials credentials) {
        getTaskConfiguration().setCredentialsTask(credentials);
    }

    /**
     * Adds a variable to the group context. The variable and it's value shall only be known later on, when the group is
     * executed.
     *
     * @param variable The variable, that shall be added.
     */
    @AntAccess
    public final void add(Variable variable) {
        getVariables().add(variable);
    }

    /**
     * This will set the failOnError flag. If this flag is set to true, then a failure to execute any individual webservice
     * will automatically interrupt the execution of the task. If set to false the failure shall be logged, but
     * the task will attempt to execute following webservice calls, not interrupting the task.
     * <p>
     * It is not expected to use this method actively, as it is specifically defined for the access by ANT.
     *
     * @param failOnError A flag indicating whether a failure to execute a webservice shall interrupt the task and prevent
     *                    the execution of following webservice operations.
     */
    @AntAccess
    public void setFailOnError(boolean failOnError) {
        getTaskConfiguration().setFailOnError(failOnError);
    }

    /**
     * This will initialize an URL pointing to the webPDF server. This information is required to locate the server.
     * This shall only contain the address and port of the server - all further URL parts will be generated automatically.
     * (i.e.: http://127.0.0.1:8080)
     * <p>
     * It is not expected to use this method actively, as it is specifically defined for the access by ANT.
     *
     * @param serverUrl An URL pointing to the webPDF server
     */
    @AntAccess
    public void setServerUrl(String serverUrl) {
        getTaskConfiguration().setServerUrl(serverUrl);
    }

    /**
     * This will set the target directory all operation results shall be published to. "Operation result" is referring to
     * the final result of called groups and will only include the results of individual webservice calls for independent and
     * isolated operations.
     * <p>
     * It is not expected to use this method actively, as it is specifically defined for the access by ANT.
     *
     * @param targetDir The path operation results shall be placed at.
     */
    @AntAccess
    public void setTargetDir(File targetDir) {
        String path = targetDir != null ? targetDir.getAbsolutePath() : " ";
        if (targetDir == null || !targetDir.exists() || !targetDir.canWrite() || !targetDir.isDirectory()) {
            String errorMessage = String.format("The given target directory could not be accessed. %s", path);
            getLogger().error(errorMessage, LogTag.WEBPDF_TASK);
            throw new BuildException(errorMessage, getLocation());
        }
        getTaskConfiguration().getTaskFiles().setTargetDirectory(targetDir);
    }

    /**
     * This will set a single file all output shall be written to. Be aware, that this shall overwrite the given file for
     * each completed sequence of operations defined in this task.
     * <p>
     * It is not expected to use this method actively, as it is specifically defined for the access by ANT.
     *
     * @param targetFile The file results shall be published to.
     */
    @AntAccess
    public void setTargetFile(File targetFile) {
        getTaskConfiguration().getTaskFiles().setTargetFile(targetFile);
    }

    /**
     * This shall define the temporary directory for this task. Results that are generated in non terminal operation
     * calls will be published to this directory and shall be deleted as soon as possible (Unless their deletion has
     * been forbidden)
     * <p>
     * It is not expected to use this method actively, as it is specifically defined for the access by ANT.
     *
     * @param tempDir The directory temporary results, of non terminal operation calls, shall be published to.
     */
    @AntAccess
    public void setTempDir(File tempDir) {
        String path = tempDir != null ? tempDir.getAbsolutePath() : " ";
        if (tempDir == null || !tempDir.exists() || !tempDir.canWrite() || !tempDir.isDirectory()) {
            String errorMsg = String.format("The given temporary directory could not be accessed. %s", path);
            getLogger().error(errorMsg, LogTag.WEBPDF_TASK);
            throw new BuildException(errorMsg, getLocation());
        }
        getTaskConfiguration().getTaskFiles().setTempDir(tempDir);
    }
}

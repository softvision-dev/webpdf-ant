package net.webpdf.ant.task;

import net.webpdf.ant.task.credentials.CredentialsFactory;
import net.webpdf.ant.task.files.IterativeTaskFileMap;
import org.apache.tools.ant.Task;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class bundles all parameters set in the top level webPDF task container.
 */
public class TaskConfiguration {
    private final IterativeTaskFileMap taskFiles = new IterativeTaskFileMap();
    private String serverURL = "http://localhost:8080//webPDF";
    private boolean failOnError = true;
    private org.apache.tools.ant.Task credentialsTask = null;

    /**
     * Shall create a new task configuration, that shall be referenced by all sub tasks.
     */
    public TaskConfiguration() {
    }

    /**
     * The returned TaskFileCollection shall contain all files the current webPDf task shall be executed for.
     *
     * @return A collection of all files, that the current webPDF task shall be executed for.
     */
    public IterativeTaskFileMap getTaskFiles() {
        return taskFiles;
    }

    /**
     * Sets the URL of the webPDF server.
     *
     * @param serverURl The url of the webPDF server.
     */
    public void setServerUrl(String serverURl) {
        this.serverURL = serverURl;
    }

    /**
     * Returns the URL of the webPDF server.
     *
     * @return The URL of the webPDF server.
     * @throws MalformedURLException Is thrown if the given server url is invalid.
     */
    public URL getServerURL() throws MalformedURLException {
        return new URL(serverURL);
    }

    /**
     * If set to true, a failing sub task shall instantly interrupt the execution of the webPDF task and shall prevent
     * other sub tasks from being executed. (DEFAULT is true)
     *
     * @param failOnError True to prevent further execution when a task has failed.
     */
    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    /**
     * If this returns true, then a failing task must interrupt the currently executed webPDF task and shall prevent
     * other sub tasks from being executed.
     *
     * @return True must lead to a total failure, if a sub task fails.
     */
    public boolean isFailOnError() {
        return failOnError;
    }

    /**
     * Initializes the credentials, that shall be used for the authentication at the webPDF server.
     * @param credentialsTask The credentials task containing all necessary authentication information.
     */
    public void setCredentialsTask(Task credentialsTask) {
        this.credentialsTask = credentialsTask;
    }

    /**
     * Returns credentials, that may be used to authenticate a user at the webPDF server.
     * @return The credentials, that have been set previously and can be used to authenticate a user at the webPDF server.
     */
    public org.apache.http.auth.Credentials getCredentials() {
        return CredentialsFactory.produceCredentials(credentialsTask);
    }
}

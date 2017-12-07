package net.webpdf.ant.task.credentials;

import net.webpdf.ant.task.AntAccess;
import org.apache.tools.ant.Task;

/**
 * This task provides credentials for windows platforms, containing username and password and optionally the name of the user's
 * workstation and domain name.
 */
public class NTCredentials extends Task {

    private String username = "";
    private String password = "";
    private String workstation = "";
    private String domain = "";

    /**
     * Sets the name of the user that shall be authenticated.
     * <p>
     * It is not expected to use this method actively, as it is specifically defined for the access by ANT.
     *
     * @param username The name of the user that shall be authenticated.
     */
    @AntAccess
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the name of the user, that shall be authenticated.
     *
     * @return The name of the user, that shall be authenticated.
     */
    String getUsername() {
        return username;
    }

    /**
     * Sets the password of the user, that shall be authenticated.
     *
     * @param password The password of the user, that shall be authenticated.
     */
    @AntAccess
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the password of the user, that shall be authenticated.
     *
     * @return The password of the user, that shall be authenticated.
     */
    String getPassword() {
        return password;
    }

    /**
     * Sets the name of the workstation of the user, that shall be authenticated.
     *
     * @param workstation The name of the workstation of the user, that shall be authenticated.
     */
    @AntAccess
    public void setWorkstation(String workstation) {
        this.workstation = workstation;
    }

    /**
     * Returns the name of the workstation of the user,that shall be authenticated.
     *
     * @return The name of the workstation of the user, that shall be authenticated
     */
    String getWorkstation() {
        return workstation;
    }

    /**
     * Sets the name of the domain of the user, that shall be authenticated.
     *
     * @param domain The name of the domain of the user, that shall be authenticated.
     */
    @AntAccess
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Returns the name of the domain of the user, that shall be authenticated.
     */
    String getDomain() {
        return domain;
    }
}

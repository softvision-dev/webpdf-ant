package net.webpdf.ant.task.credentials;

import net.webpdf.ant.task.AntAccess;
import org.apache.tools.ant.Task;
import org.jetbrains.annotations.Nullable;

/**
 * This task provides credentials for windows platforms, containing username and password and optionally the name of the user's
 * workstation and domain name.
 */
public class NTCredentials extends Task {

    @Nullable
    private String username = "";
    @Nullable
    private String password = "";
    @Nullable
    private String workstation = "";
    @Nullable
    private String domain = "";

    /**
     * Sets the name of the user that shall be authenticated.
     * <p>
     * It is not expected to use this method actively, as it is specifically defined for the access by ANT.
     *
     * @param username The name of the user that shall be authenticated.
     */
    @AntAccess
    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    /**
     * Returns the name of the user, that shall be authenticated.
     *
     * @return The name of the user, that shall be authenticated.
     */
    @Nullable
    String getUsername() {
        return username;
    }

    /**
     * Sets the password of the user, that shall be authenticated.
     *
     * @param password The password of the user, that shall be authenticated.
     */
    @AntAccess
    public void setPassword(@Nullable String password) {
        this.password = password;
    }

    /**
     * Returns the password of the user, that shall be authenticated.
     *
     * @return The password of the user, that shall be authenticated.
     */
    @Nullable
    String getPassword() {
        return password;
    }

    /**
     * Sets the name of the workstation of the user, that shall be authenticated.
     *
     * @param workstation The name of the workstation of the user, that shall be authenticated.
     */
    @AntAccess
    public void setWorkstation(@Nullable String workstation) {
        this.workstation = workstation;
    }

    /**
     * Returns the name of the workstation of the user,that shall be authenticated.
     *
     * @return The name of the workstation of the user, that shall be authenticated
     */
    @Nullable
    String getWorkstation() {
        return workstation;
    }

    /**
     * Sets the name of the domain of the user, that shall be authenticated.
     *
     * @param domain The name of the domain of the user, that shall be authenticated.
     */
    @AntAccess
    public void setDomain(@Nullable String domain) {
        this.domain = domain;
    }

    /**
     * Returns the name of the domain of the user, that shall be authenticated.
     */
    @Nullable
    String getDomain() {
        return domain;
    }

}

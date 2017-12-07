package net.webpdf.ant.task.credentials;

import net.webpdf.ant.task.AntAccess;
import org.apache.tools.ant.Task;

/**
 * This task provides basic credentials consisting of username and password.
 */
public class UserCredentials extends Task {
    private String username = "";
    private String password = "";

    /**
     * Sets the name of the user, that shall be authenticated.
     *
     * @param username The name of the user, that shall be authenticated.
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
}

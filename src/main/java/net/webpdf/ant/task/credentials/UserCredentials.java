package net.webpdf.ant.task.credentials;

import net.webpdf.ant.task.AntAccess;
import org.apache.tools.ant.Task;
import org.jetbrains.annotations.Nullable;

/**
 * This task provides basic credentials consisting of username and password.
 */
public class UserCredentials extends Task {

    @Nullable
    private String username = "";
    @Nullable
    private String password = "";

    /**
     * Sets the name of the user, that shall be authenticated.
     *
     * @param username The name of the user, that shall be authenticated.
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

}

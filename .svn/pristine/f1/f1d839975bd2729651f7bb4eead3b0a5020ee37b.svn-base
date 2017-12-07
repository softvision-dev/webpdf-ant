package net.webpdf.ant.task.credentials;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * This class creates usable Credentials, that authenticate a user during a webPDF webservice call.
 */
public class CredentialsFactory {
    /**
     * Empty private default constructor. Instantiation of this factory is never required. Call it's method from a
     * static context instead.
     */
    private CredentialsFactory() {
    }

    /**
     * This Method shall attempt to produce a usable credential instance from a given credentials task. If said task is
     * not known by this factory, it will return null instead.
     *
     * @param credentialTask The task that shall contain all authentication information.
     * @return A credentials instance, that may be used to authenticate a user during calls to the webPDF webservices.
     * @throws BuildException Shall be thrown if the task is an instance of a valid credentials task type, but did contain
     *                        invalid information.
     */
    public static org.apache.http.auth.Credentials produceCredentials(Task credentialTask) throws BuildException {
        if (credentialTask instanceof NTCredentials) {
            NTCredentials credentials = (NTCredentials) credentialTask;
            try {
                return new org.apache.http.auth.NTCredentials(credentials.getUsername(), credentials.getPassword(), credentials.getWorkstation(), credentials.getDomain());
            } catch (IllegalArgumentException ex) {
                throw new BuildException(ex);
            }
        }
        if (credentialTask instanceof UserCredentials) {
            UserCredentials credentials = (UserCredentials) credentialTask;
            try {
                return new UsernamePasswordCredentials(
                                                          credentials.getUsername(), credentials.getPassword()
                );
            } catch (IllegalArgumentException ex) {
                throw new BuildException(ex);
            }
        }

        return null;
    }
}

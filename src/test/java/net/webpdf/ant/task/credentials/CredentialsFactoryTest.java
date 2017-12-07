package net.webpdf.ant.task.credentials;

import net.webpdf.ant.WebPDFTask;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.tools.ant.BuildException;
import org.junit.Test;

import static org.junit.Assert.*;

public class CredentialsFactoryTest {
    @Test
    public void createUserCredentials() {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setUsername("usr");
        userCredentials.setPassword("pwd");
        Credentials credentials = CredentialsFactory.produceCredentials(userCredentials);
        assertTrue("Credentials should have been instance of class UsernamePasswordCredentials.", credentials instanceof UsernamePasswordCredentials);
        UsernamePasswordCredentials userPassCredentials = (UsernamePasswordCredentials) credentials;
        assertEquals("Username should have been 'usr'.", "usr", userPassCredentials.getUserName());
        assertEquals("Password should have been 'pwd'.", "pwd", userPassCredentials.getPassword());
    }

    @Test
    public void createNTCredentials() {
        NTCredentials ntCredentials = new NTCredentials();
        ntCredentials.setUsername("usr");
        ntCredentials.setPassword("pwd");
        ntCredentials.setWorkstation("workstation");
        ntCredentials.setDomain("domain");
        Credentials credentials = CredentialsFactory.produceCredentials(ntCredentials);
        assertTrue("Credentials should have been instance of class NTCredentials.", credentials instanceof org.apache.http.auth.NTCredentials);
        org.apache.http.auth.NTCredentials ntAuthCredentials = (org.apache.http.auth.NTCredentials) credentials;
        assertEquals("Username should have been 'usr'.", "usr", ntAuthCredentials.getUserName());
        assertEquals("Password should have been 'pwd'.", "pwd", ntAuthCredentials.getPassword());
        assertNotNull("User principal should have been initialized", ntAuthCredentials.getUserPrincipal());
        assertEquals("Workstation should have been 'WORKSTATION'.", "WORKSTATION", ntAuthCredentials.getWorkstation());
        assertEquals("Domain should have been 'DOMAIN'.", "DOMAIN", ntAuthCredentials.getDomain());
    }

    @Test(expected = BuildException.class)
    public void createInvalidUserCredentials() {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setUsername(null);
        userCredentials.setPassword(null);
        CredentialsFactory.produceCredentials(userCredentials);
    }

    @Test(expected = BuildException.class)
    public void createInvalidNTCredentials() {
        NTCredentials ntCredentials = new NTCredentials();
        ntCredentials.setUsername(null);
        ntCredentials.setPassword(null);
        CredentialsFactory.produceCredentials(ntCredentials);
    }

    @Test
    public void createInvalidCredentialTask() {
        assertNull("Credentials should never be provided for some invalid task.", CredentialsFactory.produceCredentials(new WebPDFTask()));
    }
}
package net.webpdf.ant;

import java.net.MalformedURLException;
import java.net.URL;

public class TestArguments {
    private static final String WEBPDF_FALLBACK = "http://localhost:8080/webPDF/";
    private static final String WEBPDF_FALLBACK_CREDS = "http://admin:admin@localhost:8080/webPDF/";

    private String protocol = getArgument(TestArgument.PROTOCOL);
    private String username = getArgument(TestArgument.USERNAME);
    private String password = getArgument(TestArgument.PASSWORD);
    private String ip = getArgument(TestArgument.SERVER_IP);
    private String port = getArgument(TestArgument.PORT);
    private String sslPort = getArgument(TestArgument.SSL_PORT);
    private String path = getArgument(TestArgument.PATH);
    private boolean useCredentials = false;
    private boolean useSSLPort = false;

    public URL buildServerUrl() throws MalformedURLException {
        if (protocol == null || username == null || password == null || ip == null || port == null || path == null || sslPort == null) {
            return useCredentials ? new URL(WEBPDF_FALLBACK_CREDS) : new URL(WEBPDF_FALLBACK);
        }
        return new URL(protocol + "://" + (useCredentials ? username + ":" + password + "@" : "")
                           + ip + ":" + (useSSLPort ? sslPort : port) + "/" + path);
    }

    public static String getArgument(TestArgument argument) {
        return System.getProperty(argument.getKey());
    }

    public TestArguments setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public TestArguments setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public TestArguments setPort(String port) {
        this.port = port;
        return this;
    }

    public TestArguments setPort(int port) {
        this.port = String.valueOf(port);
        return this;
    }

    public String getPort() {
        return port;
    }

    public TestArguments setSSLPort(String sslPort) {
        this.sslPort = sslPort;
        return this;
    }

    public TestArguments setSSLPort(int sslPort) {
        this.sslPort = String.valueOf(sslPort);
        return this;
    }

    public String getSslPort() {
        return sslPort;
    }

    public TestArguments setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public TestArguments setPath(String path) {
        this.path = path;
        return this;
    }

    public String getPath() {
        return path;
    }

    public TestArguments setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getProtocol() {
        return protocol;
    }

    public TestArguments setUseCredentials(boolean useCredentials) {
        this.useCredentials = useCredentials;
        return this;
    }

    public boolean isUseCredentials() {
        return useCredentials;
    }

    public TestArguments setUseSSLPort(boolean useSSLPort) {
        this.useSSLPort = useSSLPort;
        return this;
    }

    public boolean isUseSSLPort() {
        return useSSLPort;
    }
}

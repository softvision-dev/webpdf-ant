package net.webpdf.ant;

import java.net.MalformedURLException;
import java.net.URL;

class TestArguments {
    private static final String WEBPDF_FALLBACK = "http://localhost:8080/webPDF/";

    private String protocol = getArgument(TestArgument.PROTOCOL);
    private String username = getArgument(TestArgument.USERNAME);
    private String password = getArgument(TestArgument.PASSWORD);
    private String ip = getArgument(TestArgument.SERVER_IP);
    private String port = getArgument(TestArgument.PORT);
    private String sslPort = getArgument(TestArgument.SSL_PORT);
    private String path = getArgument(TestArgument.PATH);

    URL buildServerUrl() throws MalformedURLException {
        if (protocol == null || username == null || password == null || ip == null || port == null || path == null || sslPort == null) {
            return new URL(WEBPDF_FALLBACK);
        }
        return new URL(protocol + "://" + "" + ip + ":" + port + "/" + path);
    }

    private static String getArgument(TestArgument argument) {
        return System.getProperty(argument.getKey());
    }

    String getPassword() {
        return password;
    }

    String getUsername() {
        return username;
    }
}

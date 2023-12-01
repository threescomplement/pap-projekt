package pl.edu.pw.pap.utils;

public class UrlBuilder {
    public static String buildUrl(String endpoint, int port) {
        return "http://localhost:" + port + endpoint;
    }
}

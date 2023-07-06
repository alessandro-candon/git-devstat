/* OpenSource 2023 */
package org.devstat.gitdevstat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

public class StubImporter {

    private StubImporter() {}

    public static String getString(String fName) throws IOException {
        try (var is = StubImporter.class.getClassLoader().getResourceAsStream("fixture/" + fName)) {
            assert is != null;
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        }
    }

    public static int extractPort(String url) {
        int port = -1;
        try {
            port = new URL(url).getPort();
        } catch (MalformedURLException e) {
        }
        if (port != -1) return port;
        if (url.startsWith("https")) return 443;
        if (url.startsWith("http")) return 80;
        return -2;
    }
}

/* OpenSource 2023 */
package org.devstat.gitdevstat;

import java.util.Date;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app")
public record AppProperties(int threadPoolSize, String cloneDir, Github github, Config config) {
    public static final String APP_NAME = "gitdevstat";

    public record Github(String baseUrl, String org, String pat, String[] teams) {}

    public record Config(
            Map<String, String[]> authorIds, String[] excludedFiles, TimeFrame timeFrameDto) {}

    public record TimeFrame(Date from, Date to) {}
}

/* OpenSource 2023 */
package org.devstat.gitdevstat;

import java.time.LocalDate;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app")
public record AppProperties(
        int threadPoolSize, int maxRepoClone, String cloneDir, Github github, Config config) {
    public static final String APP_NAME = "gitdevstat";

    public record Github(String baseUrl, String org, String pat, String[] teams) {}

    public record Config(
            Map<String, String[]> authorIds,
            String[] excludedFiles,
            TimeFrame timeFrameDto,
            String[] inspect) {}

    public record TimeFrame(LocalDate from, LocalDate to) {}
}

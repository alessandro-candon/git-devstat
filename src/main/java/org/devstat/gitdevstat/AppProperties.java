/* OpenSource 2023 */
package org.devstat.gitdevstat;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app")
public record AppProperties(int threadPoolSize, String tmpDir, Github github) {
    public static final String APP_NAME = "gitdevstat";

    public record Github(String baseUrl, String org, String pat) {}
}

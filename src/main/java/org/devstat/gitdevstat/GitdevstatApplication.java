/* OpenSource 2023 */
package org.devstat.gitdevstat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GitdevstatApplication {
    private final AppProperties appProperties;

    public GitdevstatApplication(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public static void main(String[] args) {
        SpringApplication.run(GitdevstatApplication.class, args);
    }
}

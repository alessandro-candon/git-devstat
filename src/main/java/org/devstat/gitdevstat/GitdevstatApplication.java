/* OpenSource 2023 */
package org.devstat.gitdevstat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

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

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                // TODO : USE ENV VARIABLES
                .defaultHeader("Authorization", "token ".concat("mypersonaltoken"))
                .build();
    }
}

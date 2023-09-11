/* OpenSource 2023 */
package org.devstat.gitdevstat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
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
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies =
                ExchangeStrategies.builder()
                        .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                        .build();
        return WebClient.builder().exchangeStrategies(strategies).build();
    }
}

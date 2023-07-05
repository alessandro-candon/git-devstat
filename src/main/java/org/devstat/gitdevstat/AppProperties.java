package org.devstat.gitdevstat;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app")
public record AppProperties(int threadPoolSize) {
}

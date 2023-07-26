/* OpenSource 2023 */
package org.devstat.gitdevstat.client.gitprovider.github;

import java.util.Arrays;
import java.util.List;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.client.gitprovider.IGitProviderClient;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryMapper;
import org.devstat.gitdevstat.client.gitprovider.github.dto.GithubRepoDto;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GitHubClient implements IGitProviderClient {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(GitHubClient.class);

    private final AppProperties appProperties;

    private final RepositoryMapper mapper;

    public GitHubClient(AppProperties appProperties, RepositoryMapper mapper) {
        this.appProperties = appProperties;
        this.mapper = mapper;
    }

    @Override
    public List<RepositoryDto> getRepositoryList(String teamSlug) {
        GithubRepoDto[] githubRepoDtoList =
                WebClient.create(appProperties.github().baseUrl())
                        .get()
                        .uri(
                                "/orgs/"
                                        + appProperties.github().org()
                                        + "/teams/"
                                        + teamSlug
                                        + "/repos")
                        .header(HttpHeaders.CONTENT_TYPE, "application/vnd.github+json")
                        .header("X-GitHub-Api-Version", "2022-11-28")
                        .header("Authorization", "token ".concat(appProperties.github().pat()))
                        .retrieve()
                        .onStatus(
                                HttpStatus.INTERNAL_SERVER_ERROR::equals,
                                clientResponse ->
                                        Mono.error(new Exception("ERROR While getting githubrepo")))
                        .bodyToMono(GithubRepoDto[].class)
                        .block();

        assert githubRepoDtoList != null;

        log.debug("Github response: {}", githubRepoDtoList);

        return Arrays.stream(githubRepoDtoList).map(mapper::repositoryToGithubRepo).toList();
    }
}

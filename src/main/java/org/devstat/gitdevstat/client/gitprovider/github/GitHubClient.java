/* OpenSource 2023 */
package org.devstat.gitdevstat.client.gitprovider.github;

import java.util.Arrays;
import java.util.List;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.client.gitprovider.IGitProviderClient;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryMapper;
import org.devstat.gitdevstat.client.gitprovider.github.dto.GithubRepoDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GitHubClient implements IGitProviderClient {

    private final AppProperties appProperties;
    private final WebClient githubWebClient;

    private final RepositoryMapper mapper;

    public GitHubClient(
            WebClient githubWebClient, AppProperties appProperties, RepositoryMapper mapper) {
        this.githubWebClient = githubWebClient;
        this.appProperties = appProperties;
        this.mapper = mapper;
    }

    @Override
    public List<RepositoryDto> getRepositoryList(String teamSlug) {
        GithubRepoDto[] githubRepoDtoList =
                githubWebClient
                        .get()
                        .uri(
                                appProperties.github().baseUrl()
                                        + "/orgs/"
                                        + appProperties.github().org()
                                        + "/teams/"
                                        + teamSlug
                                        + "/repos")
                        .retrieve()
                        .onStatus(
                                HttpStatus.INTERNAL_SERVER_ERROR::equals,
                                clientResponse ->
                                        Mono.error(new Exception("ERROR While getting githubrepo")))
                        .bodyToMono(GithubRepoDto[].class)
                        .block();

        assert githubRepoDtoList != null;

        return Arrays.stream(githubRepoDtoList).map(mapper::repositoryToGithubRepo).toList();
    }
}

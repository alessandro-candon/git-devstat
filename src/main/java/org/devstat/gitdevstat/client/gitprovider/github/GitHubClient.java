/* OpenSource 2023 */
package org.devstat.gitdevstat.client.gitprovider.github;

import java.util.List;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.client.gitprovider.IGitProviderClient;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryMapper;
import org.devstat.gitdevstat.client.gitprovider.github.dto.GithubRepoDto;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GitHubClient implements IGitProviderClient {

    AppProperties appProperties;
    WebClient githubWebClient;

    public GitHubClient(WebClient githubWebClient) {
        this.githubWebClient = githubWebClient;
    }

    private RepositoryMapper mapper = Mappers.getMapper(RepositoryMapper.class);

    @Override
    public List<RepositoryDto> getRepositoryList(String teamSlug) {
        List<GithubRepoDto> githubRepoDtoList =
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
                                        Mono.error(new Exception("ERROR While getting switch")))
                        .bodyToMono(List.class)
                        .block();

        assert githubRepoDtoList != null;

        return githubRepoDtoList.stream().map(mapper::repositoryToGithubRepo).toList();
    }
}

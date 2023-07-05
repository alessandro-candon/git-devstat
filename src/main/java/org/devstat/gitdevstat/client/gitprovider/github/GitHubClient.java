package org.devstat.gitdevstat.client.gitprovider.github;

import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.client.gitprovider.IGitProviderClient;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryListDto;
import org.devstat.gitdevstat.client.gitprovider.github.dto.GithubRepoDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class GitHubClient implements IGitProviderClient {

    AppProperties appProperties;
    WebClient githubWebClient;

    public GitHubClient(WebClient githubWebClient) {
        this.githubWebClient = githubWebClient;
    }

    @Override
    public List<RepositoryListDto> getRepositoryList(String teamSlug) {
        GithubRepoDto[] githubRepoDtoList = githubWebClient
                .get()
                .uri(
                        appProperties.github().baseUrl()
                                + "/orgs/"
                                + appProperties.github().org()
                                + "/teams/"
                                + teamSlug
                                + "/repos"
                )
                .retrieve()
                .onStatus(
                        HttpStatus.INTERNAL_SERVER_ERROR::equals,
                        clientResponse ->
                                Mono.error(new Exception("ERROR While getting switch")))
                .bodyToMono(GithubRepoDto[].class)
                .block();

        // TODO MAP the list
        return new ArrayList<RepositoryListDto>();
    }
}
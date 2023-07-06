package org.devstat.gitdevstat.client.gitprovider.github;

import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.client.gitprovider.IGitProviderClient;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.client.gitprovider.github.dto.GithubRepoDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GitHubClient implements IGitProviderClient {

    AppProperties appProperties;
    WebClient githubWebClient;

    public GitHubClient(WebClient githubWebClient) {
        this.githubWebClient = githubWebClient;
    }

    @Override
    public List<RepositoryDto> getRepositoryList(String teamSlug) {
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

        assert githubRepoDtoList != null;
        return Arrays.stream(githubRepoDtoList).map(githubRepoDto -> new RepositoryDto(
                githubRepoDto.id(),
                githubRepoDto.name(),
                githubRepoDto.fullName()
        )).collect(Collectors.toList());
    }
}
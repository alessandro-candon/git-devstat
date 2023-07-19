/* OpenSource 2023 */
package org.devstat.gitdevstat.command;

import java.io.IOException;
import java.util.*;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.client.gitprovider.github.GitHubClient;
import org.devstat.gitdevstat.dto.GitRepositoryWithCommitResultDto;
import org.devstat.gitdevstat.git.IGitAnalyzer;
import org.devstat.gitdevstat.git.NumStatReader;
import org.devstat.gitdevstat.support.IWorkerThreadJob;
import org.devstat.gitdevstat.support.ThreadExecutor;
import org.devstat.gitdevstat.utils.FsUtil;
import org.slf4j.Logger;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class GitCommands {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(GitCommands.class);

    private final GitHubClient gitHubClient;
    private final IGitAnalyzer gitAnalyzer;
    private final NumStatReader numStatReader;
    private final FsUtil cleanerUtil;

    public GitCommands(
            ThreadExecutor threadExecutor,
            IGitAnalyzer gitAnalyzer,
            NumStatReader numStatReader,
            FsUtil cleanerUtil,
            GitHubClient gitHubClient) {
        this.threadExecutor = threadExecutor;
        this.gitAnalyzer = gitAnalyzer;
        this.numStatReader = numStatReader;
        this.cleanerUtil = cleanerUtil;
        this.gitHubClient = gitHubClient;
    }

    private final ThreadExecutor threadExecutor;

    @ShellMethod(key = "single-analysis")
    public String singleAnalysis(
            @ShellOption(defaultValue = "false") String isPrivate,
            @ShellOption(defaultValue = "git-devstat") String repoName,
            @ShellOption(defaultValue = "alessandro-candon/git-devstat") String repoFullName) {
        var repositoryDto =
                new RepositoryDto(1, repoName, repoFullName, Boolean.parseBoolean(isPrivate));
        try {
            String repoPath = gitAnalyzer.clone(repositoryDto);
            var stats = numStatReader.getCommitStatistics(repoPath);
            cleanerUtil.clearFolder();
            return stats.toString();
        } catch (Exception e) {
            log.error("", e);
            return "Error on cleaning, please do it manually";
        }
    }

    @ShellMethod(key = "analyze")
    public String runThreads(
            @ShellOption String repoTypes,
            @ShellOption String repoNames,
            @ShellOption String repoFullNames)
            throws IOException {

        String[] repos = repoNames.split(",");
        String[] reposName = repoFullNames.split(",");
        String[] reposTypesS = repoTypes.split(",");
        Boolean[] arePrivate =
                Arrays.stream(reposTypesS).map(Boolean::parseBoolean).toArray(Boolean[]::new);

        if (arePrivate.length != repos.length || repos.length != reposName.length) {
            return "Please check input parameters";
        }

        List<IWorkerThreadJob> jobs = new ArrayList<>(repos.length);

        for (int i = 0; i < repos.length; i++) {
            final int j = i;
            IWorkerThreadJob aJob =
                    () -> {
                        var repositoryDto =
                                new RepositoryDto(j, repos[j], reposName[j], arePrivate[j]);
                        String repoPath = gitAnalyzer.clone(repositoryDto);
                        var commitStatistics = numStatReader.getCommitStatistics(repoPath);
                        return new GitRepositoryWithCommitResultDto(
                                repositoryDto, commitStatistics);
                    };
            jobs.add(aJob);
        }

        List<GitRepositoryWithCommitResultDto> jobRes = threadExecutor.execute(jobs);

        cleanerUtil.clearFolder();

        return jobRes.toString();
    }

    @ShellMethod(key = "analyze-github-team-repos")
    public String runGithubTeamRepos(@ShellOption String teamNamesCsv) throws IOException {

        String[] teamNames = teamNamesCsv.split(",");

        List<IWorkerThreadJob> jobs = new ArrayList<>(teamNames.length);

        Map<Integer, RepositoryDto> repositoryDtoMap = new HashMap<>();

        for (String teamName : teamNames) {
            var repositoryListDto = this.gitHubClient.getRepositoryList(teamName);
            for (RepositoryDto repositoryDto : repositoryListDto) {
                repositoryDtoMap.put(repositoryDto.id(), repositoryDto);
            }
        }

        for (RepositoryDto repositoryDto : repositoryDtoMap.values()) {
            IWorkerThreadJob aJob =
                    () -> {
                        String repoPath = gitAnalyzer.clone(repositoryDto);
                        var commitStatistics = numStatReader.getCommitStatistics(repoPath);
                        return new GitRepositoryWithCommitResultDto(
                                repositoryDto, commitStatistics);
                    };
            jobs.add(aJob);
        }
        List<GitRepositoryWithCommitResultDto> jobRes = threadExecutor.execute(jobs);

        cleanerUtil.clearFolder();

        return jobRes.toString();
    }
}

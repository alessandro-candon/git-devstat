/* OpenSource 2023 */
package org.devstat.gitdevstat.command;

import static org.devstat.gitdevstat.git.GitHubAnalyzer.getRootStorePath;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.client.gitprovider.github.GitHubClient;
import org.devstat.gitdevstat.dto.GitRepositoryWithCommitResultDto;
import org.devstat.gitdevstat.git.GitHubAnalyzer;
import org.devstat.gitdevstat.git.IGitAnalyzer;
import org.devstat.gitdevstat.git.NumStatReader;
import org.devstat.gitdevstat.support.IWorkerThreadJob;
import org.devstat.gitdevstat.support.ThreadExecutor;
import org.devstat.gitdevstat.utils.ExportUtil;
import org.devstat.gitdevstat.view.linesofcodebyauthor.LinesOfCodeByAuthorMerger;
import org.slf4j.Logger;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class GitCommands {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(GitCommands.class);
    public static final String DEPRECATED = "DEPRECATED";

    private final AppProperties appProperties;
    private final GitHubClient gitHubClient;
    private final IGitAnalyzer gitAnalyzer;
    private final NumStatReader numStatReader;

    private final ExportUtil exportUtil;

    public GitCommands(
            AppProperties appProperties,
            ThreadExecutor threadExecutor,
            IGitAnalyzer gitAnalyzer,
            NumStatReader numStatReader,
            GitHubClient gitHubClient,
            ExportUtil exportUtil) {

        this.appProperties = appProperties;
        this.threadExecutor = threadExecutor;
        this.gitAnalyzer = gitAnalyzer;
        this.numStatReader = numStatReader;
        this.gitHubClient = gitHubClient;
        this.exportUtil = exportUtil;
    }

    boolean deprecateFilter(RepositoryDto d) {
        return d.name().indexOf(DEPRECATED) == -1;
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
            String repoPath = gitAnalyzer.getLatestInfo(repositoryDto);
            var stats = numStatReader.getCommitStatistics(repoPath);
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
                        String repoPath = gitAnalyzer.getLatestInfo(repositoryDto);
                        var commitStatistics = numStatReader.getCommitStatistics(repoPath);
                        return new GitRepositoryWithCommitResultDto(
                                repositoryDto, commitStatistics);
                    };
            jobs.add(aJob);
        }

        List<GitRepositoryWithCommitResultDto> jobRes = threadExecutor.execute(jobs);
        return jobRes.toString();
    }

    @ShellMethod(key = "analyze-github-team-repos")
    public String runGithubTeamRepos(@ShellOption String teamNamesCsv) throws IOException {

        String[] teamNames = teamNamesCsv.split(",");

        Map<Integer, RepositoryDto> repositoryDtoMap = new HashMap<>();

        for (String teamName : teamNames) {
            var repositoryListDto = this.gitHubClient.getRepositoryList(teamName);
            for (RepositoryDto repositoryDto : repositoryListDto) {
                repositoryDtoMap.put(repositoryDto.id(), repositoryDto);
            }
        }

        List<IWorkerThreadJob> jobs = prepareJobs(repositoryDtoMap);

        List<GitRepositoryWithCommitResultDto> jobRes = threadExecutor.execute(jobs);
        return jobRes.toString();
    }

    @ShellMethod(key = "analyze-from-config")
    public String analyzeFromConfig() {

        Map<Integer, RepositoryDto> repositoryDtoMap = new HashMap<>();

        for (String teamName : appProperties.github().teams()) {
            var repositoryListDto = gitHubClient.getRepositoryList(teamName);
            for (RepositoryDto repositoryDto : repositoryListDto) {
                repositoryDtoMap.put(repositoryDto.id(), repositoryDto);
            }
        }

        var repoCount =
                repositoryDtoMap.values().stream()
                        .peek(p -> log.info("* {}", p.name()))
                        .toList()
                        .size();
        log.info("Going to clone/update {} repos", repoCount);

        List<IWorkerThreadJob> jobs = prepareJobs(repositoryDtoMap);
        List<GitRepositoryWithCommitResultDto> jobRes = threadExecutor.execute(jobs);

        var linesOfCodeByAuthorMerger = new LinesOfCodeByAuthorMerger(this.appProperties);
        var result = linesOfCodeByAuthorMerger.analyze(jobRes);

        String[] order = new String[] {"AUTHORID", "ADDED", "DELETED"};
        String statFName =
                getRootStorePath(appProperties)
                        + "/"
                        + LocalDateTime.now()
                        + "-"
                        + "analyze-from-config.csv";

        try (Writer writer = Files.newBufferedWriter(Paths.get(statFName))) {
            exportUtil.serializeToCsv(writer, result.values(), order);
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException ioe) {
            log.error("Error saving stats", ioe);
        }

        if (GitHubAnalyzer.getSkippedRepos().isEmpty()) return "Analysis completed";
        else return "Partial analysis executed, skipped: " + GitHubAnalyzer.getSkippedRepos();
    }

    private List<IWorkerThreadJob> prepareJobs(Map<Integer, RepositoryDto> repositoryDtoMap) {
        List<IWorkerThreadJob> jobs = new ArrayList<>();

        for (RepositoryDto repositoryDto :
                repositoryDtoMap.values().stream().filter(this::deprecateFilter).toList()) {
            IWorkerThreadJob aJob =
                    () -> {
                        String repoPath = gitAnalyzer.getLatestInfo(repositoryDto);
                        LocalDate from =
                                appProperties
                                        .config()
                                        .timeFrameDto()
                                        .from()
                                        .minus(Period.ofDays(1));
                        LocalDate to = appProperties.config().timeFrameDto().to();
                        var commitStatistics =
                                numStatReader.getCommitStatistics(repoPath, from, to);
                        return new GitRepositoryWithCommitResultDto(
                                repositoryDto, commitStatistics);
                    };
            jobs.add(aJob);
        }

        return jobs;
    }
}

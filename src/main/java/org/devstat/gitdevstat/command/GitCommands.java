/* OpenSource 2023 */
package org.devstat.gitdevstat.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.dto.JobResult;
import org.devstat.gitdevstat.git.IGitAnalyzer;
import org.devstat.gitdevstat.git.NumStatReader;
import org.devstat.gitdevstat.git.RepoType;
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

    private final IGitAnalyzer gitAnalyzer;
    private final NumStatReader numStatReader;
    private final FsUtil cleanerUtil;

    public GitCommands(
            ThreadExecutor threadExecutor,
            IGitAnalyzer gitAnalyzer,
            NumStatReader numStatReader,
            FsUtil cleanerUtil) {
        this.threadExecutor = threadExecutor;
        this.gitAnalyzer = gitAnalyzer;
        this.numStatReader = numStatReader;
        this.cleanerUtil = cleanerUtil;
    }

    private final ThreadExecutor threadExecutor;

    @ShellMethod(key = "single-analysis")
    public String singleAnalysis(
            @ShellOption(defaultValue = "Pub") RepoType repoType,
            @ShellOption(defaultValue = "git-devstat") String repoName,
            @ShellOption(defaultValue = "alessandro-candon/git-devstat") String repoFullName) {
        var repositoryDto = new RepositoryDto(1, repoName, repoFullName, repoType);
        try {
            String repoPath = gitAnalyzer.clone(repositoryDto);
            var stats = numStatReader.getStats(repoPath);
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

        String[] reposTypesS = repoTypes.split(",");
        RepoType[] types =
                Arrays.stream(reposTypesS).map(t -> RepoType.valueOf(t)).toArray(RepoType[]::new);

        System.err.println("types are: " + types);
        String[] repos = repoNames.split(",");
        String[] reposName = repoFullNames.split(",");

        if (types.length != repos.length || repos.length != reposName.length) {
            return "Please check input parameters";
        }

        List<IWorkerThreadJob> jobs = new ArrayList<>(repos.length);

        for (int i = 0; i < repos.length; i++) {
            final int j = i;
            IWorkerThreadJob aJob =
                    () -> {
                        var repositoryDto = new RepositoryDto(j, repos[j], reposName[j], types[j]);
                        String repoPath = gitAnalyzer.clone(repositoryDto);
                        var stats = numStatReader.getStats(repoPath);
                        return new JobResult(0, stats);
                    };
            jobs.add(aJob);
        }

        List<JobResult> jobRes = threadExecutor.execute(jobs);

        cleanerUtil.clearFolder();

        return jobRes.toString();
    }
}

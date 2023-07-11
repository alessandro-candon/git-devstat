/* OpenSource 2023 */
package org.devstat.gitdevstat.command;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.dto.JobResult;
import org.devstat.gitdevstat.git.IGitAnalyzer;
import org.devstat.gitdevstat.support.IWorkerThreadJob;
import org.devstat.gitdevstat.support.NumStatReader;
import org.devstat.gitdevstat.support.ThreadExecutor;
import org.devstat.gitdevstat.utils.FsUtil;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class GitCommands {

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
            @ShellOption(defaultValue = "git-devstat") String repoName,
            @ShellOption(defaultValue = "alessandro-candon/git-devstat") String repoFullName) {
        var repositoryDto = new RepositoryDto(123, repoName, repoFullName);

        var aggResStr = "";
        try {
            String repoPath = gitAnalyzer.clone(repositoryDto);
            numStatReader.prepareProcess(repoPath);
            var stats = numStatReader.read();
            var aggRes = numStatReader.aggregateByAuthor(stats);
            aggResStr = aggRes.toString();
            cleanerUtil.clearFolder();

        } catch (IOException e) {
            return "Error on cleaning, please do it manually";
        }
        return aggResStr;
    }

    @ShellMethod(key = "runThreads")
    public String runThreads() {
        IWorkerThreadJob aJob =
                () -> {
                    // make some work
                    return new JobResult(0, "Job done");
                };

        List<IWorkerThreadJob> jobs = Collections.nCopies(10, aJob);
        List<JobResult> jobRes = threadExecutor.execute(jobs);

        return jobRes.toString();
    }
}

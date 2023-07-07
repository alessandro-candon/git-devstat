/* OpenSource 2023 */
package org.devstat.gitdevstat.command;

import java.util.Collections;
import java.util.List;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.dto.JobResult;
import org.devstat.gitdevstat.git.IGitAnalyzer;
import org.devstat.gitdevstat.support.IWorkerThreadJob;
import org.devstat.gitdevstat.support.ThreadExecutor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class GitCommands {

    private IGitAnalyzer gitAnalyzer;

    public GitCommands(ThreadExecutor threadExecutor, IGitAnalyzer gitAnalyzer) {
        this.threadExecutor = threadExecutor;
        this.gitAnalyzer = gitAnalyzer;
    }

    private final ThreadExecutor threadExecutor;

    @ShellMethod(key = "run")
    public String run() {
        var repositoryDto = new RepositoryDto(123, "", "");
        gitAnalyzer.clone(repositoryDto);
        return "Done";
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

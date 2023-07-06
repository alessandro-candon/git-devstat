/* OpenSource 2023 */
package org.devstat.gitdevstat.command;

import java.util.Collections;
import java.util.List;
import org.devstat.gitdevstat.dto.JobResult;
import org.devstat.gitdevstat.support.IWorkerThreadJob;
import org.devstat.gitdevstat.support.ThreadExecutor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class GitCommands {

    public GitCommands(ThreadExecutor threadExecutor) {
        this.threadExecutor = threadExecutor;
    }

    private final ThreadExecutor threadExecutor;

    @ShellMethod(key = "hello-world")
    public String helloWorld(@ShellOption(defaultValue = "git") String arg) {
        return "Hello world " + arg;
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

/* OpenSource 2023 */
package org.devstat.gitdevstat.command;

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
        IWorkerThreadJob aJob = () -> "Job done";
         threadExecutor.execute(aJob);
         return "done";
    }
}

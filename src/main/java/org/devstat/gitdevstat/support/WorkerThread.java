/* OpenSource 2023 */
package org.devstat.gitdevstat.support;

import java.util.concurrent.Callable;
import org.devstat.gitdevstat.dto.GitRepositoryWithCommitResultDto;
import org.slf4j.Logger;

public class WorkerThread implements Callable<GitRepositoryWithCommitResultDto> {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(WorkerThread.class);

    private IWorkerThreadJob job;

    public WorkerThread(IWorkerThreadJob job) {
        this.job = job;
    }

    @Override
    public GitRepositoryWithCommitResultDto call() {
        log.trace("{} Starting...", Thread.currentThread().getName());
        var res = job.processCommand();
        log.trace("{} Finished", Thread.currentThread().getName());
        return res;
    }
}

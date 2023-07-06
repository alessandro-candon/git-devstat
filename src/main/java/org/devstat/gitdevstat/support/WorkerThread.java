/* OpenSource 2023 */
package org.devstat.gitdevstat.support;

import java.util.concurrent.Callable;
import org.devstat.gitdevstat.dto.JobResult;
import org.slf4j.Logger;

public class WorkerThread implements Callable<JobResult> {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(WorkerThread.class);

    private IWorkerThreadJob job;

    public WorkerThread(IWorkerThreadJob job) {
        this.job = job;
    }

    @Override
    public JobResult call() {
        log.info("{} Starting...", Thread.currentThread().getName());
        var res = job.processCommand();
        log.info("{} Finished", Thread.currentThread().getName());
        return res;
    }
}

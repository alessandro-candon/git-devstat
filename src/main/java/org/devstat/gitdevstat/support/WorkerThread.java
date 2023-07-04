package org.devstat.gitdevstat.support;

import org.slf4j.Logger;

public class WorkerThread implements Runnable {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(WorkerThread.class);

    private IWorkerThreadJob job;

    public WorkerThread(IWorkerThreadJob job) {
        this.job = job;
    }

    @Override
    public void run() {
        log.info(  "{} Starting...",Thread.currentThread().getName());
        job.processCommand();
        log.info(  "{} Finished",Thread.currentThread().getName());
    }

}

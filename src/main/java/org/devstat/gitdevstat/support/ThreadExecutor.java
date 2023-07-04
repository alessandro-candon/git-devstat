package org.devstat.gitdevstat.support;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ThreadExecutor {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ThreadExecutor.class);

    public List<String> execute(IWorkerThreadJob aJob) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            Runnable worker = new WorkerThread(aJob);
            executor.execute(worker);
        }
        executor.shutdown();

        while (!executor.isTerminated()) {
        }

        log.info("Finished all threads");
        return List.of("1","...","10");
    }
}

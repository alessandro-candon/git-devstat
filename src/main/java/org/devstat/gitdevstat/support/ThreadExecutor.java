/* OpenSource 2023 */
package org.devstat.gitdevstat.support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.dto.JobResult;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ThreadExecutor {
    AppProperties appProperties;

    public ThreadExecutor(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ThreadExecutor.class);

    public List<JobResult> execute(List<IWorkerThreadJob> jobs) {
        ExecutorService executor = Executors.newFixedThreadPool(appProperties.threadPoolSize());

        List<Future<JobResult>> futures = new ArrayList<>();
        for (IWorkerThreadJob aJob : jobs) {
            futures.add(executor.submit(new WorkerThread(aJob)));
        }
        executor.shutdown();

        List<JobResult> ret =
                futures.stream()
                        .map(
                                job -> {
                                    try {
                                        return job.get();
                                    } catch (InterruptedException | ExecutionException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                })
                        .toList();

        log.info("Finished all threads");
        return ret;
    }
}

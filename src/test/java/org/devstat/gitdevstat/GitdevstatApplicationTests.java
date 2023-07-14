/* OpenSource 2023 */
package org.devstat.gitdevstat;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.devstat.gitdevstat.dto.JobResult;
import org.devstat.gitdevstat.support.IWorkerThreadJob;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GitdevstatApplicationTests {

    public static List<IWorkerThreadJob> get10WorkerThreadJobs(int nCopies) {
        IWorkerThreadJob aJob =
                () -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new JobResult(-1, Map.of());
                };

        List<IWorkerThreadJob> jobs = Collections.nCopies(nCopies, aJob);
        return jobs;
    }

    @Test
    void contextLoads() {}
}

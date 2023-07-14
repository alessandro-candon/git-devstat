/* OpenSource 2023 */
package org.devstat.gitdevstat.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.devstat.gitdevstat.GitdevstatApplicationTests;
import org.devstat.gitdevstat.dto.JobResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Thread executor test")
public class ThreadExecutorTest {

    @Autowired private ThreadExecutor threadExecutor;

    @Test
    @Timeout(3)
    void testParallelIsWorking() {
        List<IWorkerThreadJob> jobs = GitdevstatApplicationTests.get10WorkerThreadJobs(10);
        List<JobResult> jobRes = threadExecutor.execute(jobs);
        assertThat(jobRes).isNotNull().contains(new JobResult(-1, Map.of())).size().isEqualTo(10);
    }
}

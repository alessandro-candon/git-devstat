/* OpenSource 2023 */
package org.devstat.gitdevstat.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.devstat.gitdevstat.GitdevstatApplicationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@DisplayName("Thread executor no multithreading")
@TestPropertySource(properties = {"app.threadPoolSize=1"})
public class ThreadExecutorFailTest {

    @Autowired private ThreadExecutor threadExecutor;

    @Test
    void testParallelIsNotWorking() {
        List<IWorkerThreadJob> jobs = GitdevstatApplicationTests.get10WorkerThreadJobs(5);
        long start = System.currentTimeMillis();
        threadExecutor.execute(jobs);
        assertThat(System.currentTimeMillis() - start).isGreaterThan(5000l);
    }
}

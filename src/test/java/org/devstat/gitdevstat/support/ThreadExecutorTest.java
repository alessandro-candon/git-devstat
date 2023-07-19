/* OpenSource 2023 */
package org.devstat.gitdevstat.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.devstat.gitdevstat.GitdevstatApplicationTests;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.dto.GitRepositoryWithCommitResultDto;
import org.devstat.gitdevstat.git.RepoType;
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
        List<GitRepositoryWithCommitResultDto> jobRes = threadExecutor.execute(jobs);
        var repositoryDto = new RepositoryDto(1, "", "", RepoType.Pub);
        assertThat(jobRes)
                .isNotNull()
                .contains(new GitRepositoryWithCommitResultDto(repositoryDto, Map.of()))
                .size()
                .isEqualTo(10);
    }
}

/* OpenSource 2023 */
package org.devstat.gitdevstat.git;

import static org.devstat.gitdevstat.AppProperties.APP_NAME;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.RepoCleanerSpringBootTest;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.git.dto.GitCommitResultDto;
import org.devstat.gitdevstat.git.dto.StatInfoWithPathDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GitHubAnalyzerTest extends RepoCleanerSpringBootTest {

    @Autowired AppProperties appProperties;

    @Autowired GitHubAnalyzer gitHubAnalyzer;

    @Test
    void testClone() {
        var repositoryDto =
                new RepositoryDto(1, "git-devstat", "alessandro-candon/git-devstat", false);
        gitHubAnalyzer.getLatestInfo(repositoryDto);
        File f =
                new File(
                        appProperties.cloneDir()
                                + "/"
                                + APP_NAME
                                + "/"
                                + repositoryDto.name()
                                + "/README.md");
        assertTrue(f.exists());
    }

    @Test
    void testAnalyze() throws IOException {
        var repositoryDto =
                new RepositoryDto(1, "git-devstat", "alessandro-candon/git-devstat", false);
        gitHubAnalyzer.getLatestInfo(repositoryDto);
        var res = gitHubAnalyzer.stat(repositoryDto);
        assertNotNull(res);
        assertTrue(res.size() >= 24);
        assertNotNull(res.get("7fa884d"));
        assertEquals(
                new GitCommitResultDto(
                        "7fa884d",
                        "dependabot[bot]",
                        "49699333+dependabot[bot]@users.noreply.github.com",
                        "49699333+dependabot[bot]",
                        "Mon, 17 Jul 2023 01:40:14 +0000",
                        1689558014,
                        "GitHub",
                        "noreply@github.com",
                        "Mon, 17 Jul 2023 01:40:14 +0000",
                        1689558014,
                        "build-deps-bump-io.spring.dependency-management-from-1.1.0-to-1.1.1",
                        Map.of(
                                "\t1\tbuild.gradle",
                                new StatInfoWithPathDto("\t1\tbuild.gradle", 1, 1))),
                res.get("7fa884d"));
    }
}

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
        gitHubAnalyzer.clone(repositoryDto);
        File f =
                new File(
                        appProperties.tmpDir()
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
        gitHubAnalyzer.clone(repositoryDto);
        var res = gitHubAnalyzer.stat(repositoryDto);
        assertNotNull(res);
        assertTrue(res.size() >= 24);
        assertNotNull(res.get("e6f8862"));
        assertEquals(
                new GitCommitResultDto(
                        "e6f8862",
                        "Cesare Mauri",
                        "cesare.mauri@decathlon.com",
                        "cesare.mauri",
                        "Tue, 4 Jul 2023 18:02:20 +0200",
                        1688486540,
                        "Cesare Mauri",
                        "cesare.mauri@decathlon.com",
                        "Tue, 4 Jul 2023 18:02:20 +0200",
                        1688486540,
                        "Stay-update",
                        Map.of(
                                "0\t0\t.github/dependabot.yml",
                                new StatInfoWithPathDto("0\t0\t.github/dependabot.yml", 10, 0))),
                res.get("e6f8862"));
    }
}

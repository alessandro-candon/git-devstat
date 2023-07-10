/* OpenSource 2023 */
package org.devstat.gitdevstat.git;

import static org.devstat.gitdevstat.AppProperties.APP_NAME;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.RepoCleanerSpringBootTest;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GitHubAnalyzerTest extends RepoCleanerSpringBootTest {

    @Autowired AppProperties appProperties;

    @Autowired GitHubAnalyzer gitHubAnalyzer;

    @Test
    void testClone() {
        var repositoryDto = new RepositoryDto(1, "git-devstat", "alessandro-candon/git-devstat");
        this.gitHubAnalyzer.clone(repositoryDto);
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
    void testAnalyze() throws IOException, GitAPIException {
        var repositoryDto = new RepositoryDto(1, "git-devstat", "alessandro-candon/git-devstat");
        this.gitHubAnalyzer.clone(repositoryDto);
        this.gitHubAnalyzer.stat(repositoryDto);
    }
}

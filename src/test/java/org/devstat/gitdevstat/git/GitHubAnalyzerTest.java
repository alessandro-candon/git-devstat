/* OpenSource 2023 */
package org.devstat.gitdevstat.git;

import static org.devstat.gitdevstat.AppProperties.APP_NAME;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.utils.FsUtil;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GitHubAnalyzerTest {

    @Autowired AppProperties appProperties;

    @Autowired GitHubAnalyzer gitHubAnalyzer;

    @Autowired FsUtil fs;

    @BeforeEach
    void setUp() throws IOException {
        fs.clearFolder();
    }

    @AfterEach
    void tearDown() throws IOException {
        fs.clearFolder();
    }

    @Test
    void testClone() throws IOException {
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

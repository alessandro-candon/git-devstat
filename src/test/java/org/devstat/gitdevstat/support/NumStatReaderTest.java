/* OpenSource 2023 */
package org.devstat.gitdevstat.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.devstat.gitdevstat.RepoCleanerSpringBootTest;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.git.GitHubAnalyzer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

class NumStatReaderTest extends RepoCleanerSpringBootTest {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(WorkerThread.class);

    @Autowired GitHubAnalyzer gitHubAnalyzer;

    @Autowired NumStatReader numStatReader;

    @Test
    void testParseHistory() throws IOException {
        var repositoryDto = new RepositoryDto(1, "git-devstat", "alessandro-candon/git-devstat");
        String repoPath = gitHubAnalyzer.clone(repositoryDto);

        numStatReader.prepareProcess(repoPath);
        var stats = numStatReader.read();

        System.out.println(stats);

        var firstStat = stats.get("2d014f1");
        assertEquals(firstStat.ae(), "alessandro.candon@decathlon.com");
    }
}

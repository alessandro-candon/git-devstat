/* OpenSource 2023 */
package org.devstat.gitdevstat.git;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import org.devstat.gitdevstat.RepoCleanerSpringBootTest;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.git.dto.StatInfoWithPathDto;
import org.devstat.gitdevstat.support.WorkerThread;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

class NumStatReaderTest extends RepoCleanerSpringBootTest {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(WorkerThread.class);

    @Autowired GitHubAnalyzer gitHubAnalyzer;

    @Autowired NumStatReader numStatReader;

    @Test
    void testParseHistory() throws IOException {
        var repositoryDto =
                new RepositoryDto(1, "git-devstat", "alessandro-candon/git-devstat", false);
        String repoPath = gitHubAnalyzer.clone(repositoryDto);
        var stats = numStatReader.getCommitStatistics(repoPath);
        var firstStat = stats.get("2d014f1");
        assertNotNull(firstStat);
        assertEquals("2d014f1", firstStat.h());
        assertEquals("alessandro.candon@test.com", firstStat.ae());
        assertNotNull(firstStat.statInfoDtoHashMap());
        assertEquals(1, firstStat.statInfoDtoHashMap().size());
        String statKey = "\t1\tsrc/main/java/org/devstat/gitdevstat/git/GitHubAnalyzer.java";
        assertNotNull(firstStat.statInfoDtoHashMap().get(statKey));
        assertEquals(
                new StatInfoWithPathDto(statKey, 3, 1),
                firstStat.statInfoDtoHashMap().get(statKey));
    }
}

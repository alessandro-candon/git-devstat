/* OpenSource 2023 */
package org.devstat.gitdevstat.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.devstat.gitdevstat.RepoCleanerSpringBootTest;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.git.GitHubAnalyzer;
import org.devstat.gitdevstat.git.dto.StatInfoDto;
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

        // log for develop
        for (Map.Entry<String, Map<String, StatInfoDto>> stat : stats.entrySet()) {
            log.debug("K: {}", stat.getKey());
            for (Map.Entry<String, StatInfoDto> entryStat : stat.getValue().entrySet()) {
                log.debug("---- {} * {}", entryStat.getKey(), entryStat.getValue());
            }
        }

        assertNotNull(stats);
        assertEquals(23, stats.keySet().size());
    }

    @Test
    void testAggregator() {
        Map<String, Map<String, StatInfoDto>> stats = new HashMap<>();

        Map<String, StatInfoDto> e1 =
                Map.of(
                        "1) file1",
                        new StatInfoDto(3, 4),
                        "1) file2",
                        new StatInfoDto(0, 1),
                        "3) file2",
                        new StatInfoDto(0, 5));
        Map<String, StatInfoDto> e2 = Map.of("2) file1", new StatInfoDto(2, 0));

        stats.put("User1", e1);
        stats.put("User2", e2);
        stats.put("User3", Map.of());

        var res = numStatReader.aggregateByAuthor(stats);

        assertNotNull(res);
        assertEquals(3, res.size());
        assertEquals(new StatInfoDto(3, 10), res.get("User1"));
        assertEquals(new StatInfoDto(2, 0), res.get("User2"));
        assertEquals(new StatInfoDto(0, 0), res.get("User3"));
    }
}

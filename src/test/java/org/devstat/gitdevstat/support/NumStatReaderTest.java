/* OpenSource 2023 */
package org.devstat.gitdevstat.support;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.devstat.gitdevstat.RepoCleanerSpringBootTest;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.git.GitHubAnalyzer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NumStatReaderTest extends RepoCleanerSpringBootTest {

    @Autowired GitHubAnalyzer gitHubAnalyzer;

    @Autowired NumStatReader numstat;

    @Test
    public void testParseHistory() throws IOException {
        var repositoryDto = new RepositoryDto(1, "git-devstat", "alessandro-candon/git-devstat");
        String repoPath = gitHubAnalyzer.clone(repositoryDto);

        numstat.prepareProcess(repoPath);
        var stats = numstat.read();

        // log for develop
        for (Map.Entry<String, HashMap<String, NumStatReader.StatInfo>> stat : stats.entrySet()) {
            System.err.println("K: " + stat.getKey());
            for (Map.Entry<String, NumStatReader.StatInfo> entryStat : stat.getValue().entrySet()) {
                System.err.println("---- " + entryStat.getKey() + " * " + entryStat.getValue());
            }
        }

        assertNotNull(stats);
        assertEquals(3, stats.keySet().size());
        assertEquals(
                new NumStatReader.StatInfo(8, 0),
                stats.get("Cesare Mauri").get("5) src/test/resources/application.yaml"));
    }
}

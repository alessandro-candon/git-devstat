/* OpenSource 2023 */
package org.devstat.gitdevstat.client.gitprovider.github;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.StubImporter;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GitHubClientTest {

    static MockWebServer mockBackEnd;

    @Autowired AppProperties appProperties;

    @Autowired GitHubClient gitHubClient;

    @BeforeEach
    void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(StubImporter.extractPort(appProperties.github().baseUrl()));
    }

    @AfterEach
    void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void testRepositoryList() throws IOException, InterruptedException {
        mockBackEnd.enqueue(
                new MockResponse()
                        .setBody(StubImporter.getString("GithubRepo.json"))
                        .addHeader("Content-Type", "application/json"));

        var res = gitHubClient.getRepositoryList("teamSlug");
        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        assertThat(res).contains(new RepositoryDto(-1, "-1 repo", "-1 repo fullname", true));
        assertThat(res).contains(new RepositoryDto(-2, "-2 repo", "-2 repo fullname", true));
    }
}

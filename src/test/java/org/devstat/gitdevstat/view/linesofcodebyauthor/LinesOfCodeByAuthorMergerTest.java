/* OpenSource 2023 */
package org.devstat.gitdevstat.view.linesofcodebyauthor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.dto.GitRepositoryWithCommitResultDto;
import org.devstat.gitdevstat.git.dto.GitCommitResultDto;
import org.devstat.gitdevstat.git.dto.StatInfoWithPathDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LinesOfCodeByAuthorMergerTest {

    private static LinesOfCodeByAuthorMerger linesOfCodeByAuthorMerger;

    @BeforeAll
    static void setup() {
        String[] githubTeams = {"tacos"};
        Map<String, String[]> authorIds = new HashMap<>();
        authorIds.put("cesare-mauri", new String[] {"cesare.mauri@test.com", ""});
        String[] excludedFiles = {""};
        var githubAppProp = new AppProperties.Github("", "", "", githubTeams);
        var configAppProp = new AppProperties.Config(authorIds, excludedFiles, null);
        AppProperties appProperties = new AppProperties(1, "/tmp", githubAppProp, configAppProp);

        linesOfCodeByAuthorMerger = new LinesOfCodeByAuthorMerger(appProperties);
    }

    private static final String formattedCommit =
            "e3696af|Cesare Mauri|cesare.mauri@test.com|cesare.mauri|Tue, 11 Jul 2023"
                    + " 12:16:00 +0200|1689070560|Cesare Mauri|cesare.mauri@test.com|Tue, 11"
                    + " Jul 2023 12:16:00 +0200|1689070560|Add-netstat-in-main-app";

    @Test
    void analyze() {
        List<GitRepositoryWithCommitResultDto> gitRepositoryWithCommitResultDtoList =
                new ArrayList<>();

        var repositoryDto = new RepositoryDto(-1, "repo-dto", "alessandro-candon/repo-dto", false);
        var resultData = new HashMap<String, GitCommitResultDto>();

        var gitCommitResultDto = new GitCommitResultDto.Builder(formattedCommit).build();
        StatInfoWithPathDto i = new StatInfoWithPathDto("\t1\tfilePath", 1, 1);
        gitCommitResultDto.statInfoDtoHashMap().put("filePath", i);
        resultData.put("e3696af", gitCommitResultDto);
        var gitRepositoryWithCommitResultDto =
                new GitRepositoryWithCommitResultDto(repositoryDto, resultData);

        gitRepositoryWithCommitResultDtoList.add(gitRepositoryWithCommitResultDto);

        var result = linesOfCodeByAuthorMerger.analyze(gitRepositoryWithCommitResultDtoList);

        assertThat(result).containsKey("cesare-mauri");
    }

    @Test
    void filterTest() {
        String f1Name = "1\t2\tREADME.md";
        String f2Name = "10\t20\tsrc/main/java/org/devstat/gitdevstat/AppProperties.java";
        String f3Name = "100\t200\tsrc/main/java/org/devstat/gitdevstat/command/GitCommands.java";

        GitCommitResultDto r1 = new GitCommitResultDto.Builder(formattedCommit).build();
        StatInfoWithPathDto s1 = new StatInfoWithPathDto(f1Name, 1, 2);
        StatInfoWithPathDto s2 = new StatInfoWithPathDto(f2Name, 10, 20);
        StatInfoWithPathDto s3 = new StatInfoWithPathDto(f3Name, 100, 200);

        r1.statInfoDtoHashMap()
                .putAll(Map.of(s1.filePath(), s1, s2.filePath(), s2, s3.filePath(), s3));

        Map.Entry<String, GitCommitResultDto> entry =
                (Map.Entry<String, GitCommitResultDto>)
                        Map.of("entry1", r1).entrySet().toArray()[0];

        // spotless:off
        assertThat(linesOfCodeByAuthorMerger.countLines(entry, List.of(),StatInfoWithPathDto::added)).isEqualTo(111);
        assertThat(linesOfCodeByAuthorMerger.countLines(entry, List.of("README.md"),StatInfoWithPathDto::added)).isEqualTo(110);
        assertThat(linesOfCodeByAuthorMerger.countLines(entry, List.of("src"),StatInfoWithPathDto::added)).isEqualTo(1);

        assertThat(linesOfCodeByAuthorMerger.countLines(entry, List.of(),StatInfoWithPathDto::deleted)).isEqualTo(222);
        assertThat(linesOfCodeByAuthorMerger.countLines(entry, List.of("README.md"),StatInfoWithPathDto::deleted)).isEqualTo(220);
        assertThat(linesOfCodeByAuthorMerger.countLines(entry, List.of("src"),StatInfoWithPathDto::deleted)).isEqualTo(2);
        // spotless:on

    }
}

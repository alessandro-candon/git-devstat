/* OpenSource 2023 */
package org.devstat.gitdevstat.view.linesofcodebyauthor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.dto.GitHubAnalyzerConfigurationDto;
import org.devstat.gitdevstat.dto.GitRepositoryWithCommitResultDto;
import org.devstat.gitdevstat.git.dto.GitCommitResultDto;
import org.devstat.gitdevstat.git.dto.StatInfoWithPathDto;
import org.junit.jupiter.api.Test;

class LinesOfCodeByAuthorMergerTest {

    @Test
    void analyze() {
        String[] githubTeams = {"tacos"};
        HashMap<String, String[]> authorIds = new HashMap<>();
        authorIds.put("alessandro-candon", new String[] {"", ""});
        String[] excludedFiles = {""};
        var gitHubAnalyzerConfigurationDto =
                new GitHubAnalyzerConfigurationDto(githubTeams, authorIds, excludedFiles);

        List<GitRepositoryWithCommitResultDto> gitRepositoryWithCommitResultDtoList =
                new ArrayList<>();

        var repositoryDto = new RepositoryDto(-1, "repo-dto", "alessandro-candon/repo-dto", false);
        var resultData = new HashMap<String, GitCommitResultDto>();
        var formattedCommit =
                "e3696af|Cesare Mauri|cesare.mauri@test.com|cesare.mauri|Tue, 11 Jul 2023"
                        + " 12:16:00 +0200|1689070560|Cesare Mauri|cesare.mauri@test.com|Tue, 11"
                        + " Jul 2023 12:16:00 +0200|1689070560|Add-netstat-in-main-app";
        var gitCommitResultDto = new GitCommitResultDto.Builder(formattedCommit).build();
        StatInfoWithPathDto i = new StatInfoWithPathDto("filePath", 1, 1);
        gitCommitResultDto.statInfoDtoHashMap().put("filePath", i);
        resultData.put("e3696af", gitCommitResultDto);
        var gitRepositoryWithCommitResultDto =
                new GitRepositoryWithCommitResultDto(repositoryDto, resultData);

        gitRepositoryWithCommitResultDtoList.add(gitRepositoryWithCommitResultDto);

        var linesOfCodeByAuthor = new LinesOfCodeByAuthorMerger(gitHubAnalyzerConfigurationDto);

        var result = linesOfCodeByAuthor.analyze(gitRepositoryWithCommitResultDtoList);
    }
}

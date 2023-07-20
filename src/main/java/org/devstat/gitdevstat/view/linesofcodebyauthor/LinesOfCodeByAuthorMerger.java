/* OpenSource 2023 */
package org.devstat.gitdevstat.view.linesofcodebyauthor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.devstat.gitdevstat.dto.GitHubAnalyzerConfigurationDto;
import org.devstat.gitdevstat.dto.GitRepositoryWithCommitResultDto;

public class LinesOfCodeByAuthorMerger {

    private GitHubAnalyzerConfigurationDto gitHubAnalyzerConfigurationDto;

    LinesOfCodeByAuthorMerger(GitHubAnalyzerConfigurationDto gitHubAnalyzerConfigurationDto) {
        this.gitHubAnalyzerConfigurationDto = gitHubAnalyzerConfigurationDto;
    }

    public Map<String, LinesOfCodeByAuthorDto> analyze(
            List<GitRepositoryWithCommitResultDto> gitRepositoryWithCommitResultDtoList) {

        return new HashMap<>();
    }
}

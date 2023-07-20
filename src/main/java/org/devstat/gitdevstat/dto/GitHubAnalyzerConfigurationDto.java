/* OpenSource 2023 */
package org.devstat.gitdevstat.dto;

import java.util.Map;

public class GitHubAnalyzerConfigurationDto extends AnalyzerConfigurationDto {
    private final String[] githubTeams;

    public GitHubAnalyzerConfigurationDto(
            String[] githubTeams, Map<String, String[]> authorIds, String[] excludedFiles) {
        super(authorIds, excludedFiles);
        this.githubTeams = githubTeams;
    }

    public GitHubAnalyzerConfigurationDto(
            String[] githubTeams,
            Map<String, String[]> authorIds,
            String[] excludedFiles,
            TimeFrameDto timeFrameDto) {
        super(authorIds, excludedFiles, timeFrameDto);
        this.githubTeams = githubTeams;
    }

    public String[] getGithubTeams() {
        return githubTeams;
    }
}

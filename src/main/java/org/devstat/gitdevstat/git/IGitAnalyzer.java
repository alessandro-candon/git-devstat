/* OpenSource 2023 */
package org.devstat.gitdevstat.git;

import java.io.IOException;
import java.util.Map;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.git.dto.GitCommitResultDto;

public interface IGitAnalyzer {
    String getLatestInfo(RepositoryDto repositoryDto);

    Map<String, GitCommitResultDto> stat(RepositoryDto repositoryDto) throws IOException;
}

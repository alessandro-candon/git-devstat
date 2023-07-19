/* OpenSource 2023 */
package org.devstat.gitdevstat.dto;

import java.util.Map;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.git.dto.GitCommitResultDto;

public record GitRepositoryWithCommitResultDto(
        RepositoryDto repositoryDto, Map<String, GitCommitResultDto> resultData) {}

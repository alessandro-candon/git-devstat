/* OpenSource 2023 */
package org.devstat.gitdevstat.client.gitprovider.dto;

import org.devstat.gitdevstat.client.gitprovider.github.dto.GithubRepoDto;
import org.mapstruct.Mapper;

@Mapper
public interface RepositoryMapper {
    RepositoryDto repositoryToGithubRepo(GithubRepoDto source);

    GithubRepoDto githubRepoToRepository(RepositoryDto destination);
}

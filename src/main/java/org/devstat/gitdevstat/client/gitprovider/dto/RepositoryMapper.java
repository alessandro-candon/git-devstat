/* OpenSource 2023 */
package org.devstat.gitdevstat.client.gitprovider.dto;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.devstat.gitdevstat.client.gitprovider.github.dto.GithubRepoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING)
public interface RepositoryMapper {
    @Mapping(target = "repoType", constant = "Priv")
    RepositoryDto repositoryToGithubRepo(GithubRepoDto source);

    GithubRepoDto githubRepoToRepository(RepositoryDto destination);
}

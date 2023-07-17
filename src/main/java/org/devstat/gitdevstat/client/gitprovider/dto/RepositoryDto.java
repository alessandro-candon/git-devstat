/* OpenSource 2023 */
package org.devstat.gitdevstat.client.gitprovider.dto;

import org.devstat.gitdevstat.git.RepoType;

public record RepositoryDto(int id, String name, String fullName, RepoType repoType) {}

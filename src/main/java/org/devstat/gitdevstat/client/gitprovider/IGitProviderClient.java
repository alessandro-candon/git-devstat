package org.devstat.gitdevstat.client.gitprovider;

import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;

import java.util.List;

public interface IGitProviderClient {
    public List<RepositoryDto> getRepositoryList(String teamSlug);
}

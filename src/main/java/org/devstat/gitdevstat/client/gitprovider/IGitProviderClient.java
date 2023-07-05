package org.devstat.gitdevstat.client.gitprovider;

import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryListDto;

import java.util.List;

public interface IGitProviderClient {
    public List<RepositoryListDto> getRepositoryList(String teamSlug);
}

/* OpenSource 2023 */
package org.devstat.gitdevstat.git;

import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;

public interface IGitAnalyzer {
    void clone(RepositoryDto repositoryDto);
}

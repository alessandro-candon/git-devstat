/* OpenSource 2023 */
package org.devstat.gitdevstat.git;

import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;

public interface IGitAnalyzer {

    public void clone(RepositoryDto repositoryDto);
}

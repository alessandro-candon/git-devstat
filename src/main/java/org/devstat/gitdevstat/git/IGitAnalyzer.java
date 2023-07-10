/* OpenSource 2023 */
package org.devstat.gitdevstat.git;

import java.io.IOException;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.eclipse.jgit.api.errors.GitAPIException;

public interface IGitAnalyzer {
    void clone(RepositoryDto repositoryDto);

    void stat(RepositoryDto repositoryDto) throws IOException, GitAPIException;
}

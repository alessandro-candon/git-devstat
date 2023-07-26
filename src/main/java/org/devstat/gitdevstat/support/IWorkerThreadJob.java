/* OpenSource 2023 */
package org.devstat.gitdevstat.support;

import org.devstat.gitdevstat.dto.GitRepositoryWithCommitResultDto;

public interface IWorkerThreadJob {
    public GitRepositoryWithCommitResultDto processCommand();
}

/* OpenSource 2023 */
package org.devstat.gitdevstat.git;

import java.io.File;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

@Service
public class GitHubAnalyzer implements IGitAnalyzer {

    AppProperties appProperties;

    public void clone(RepositoryDto repositoryDto) {
        CloneCommand cloneCommand = Git.cloneRepository();
        cloneCommand.setURI("https://github.com/".concat(repositoryDto.fullName()));
        cloneCommand.setCredentialsProvider(
                new UsernamePasswordCredentialsProvider(appProperties.github().pat(), ""));
        cloneCommand.setDirectory(new File("/tmp/gitdevstat/".concat(repositoryDto.name())));
        try {
            cloneCommand.call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }
}

/* OpenSource 2023 */
package org.devstat.gitdevstat.git;

import static org.devstat.gitdevstat.AppProperties.APP_NAME;

import java.io.File;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.utils.FsUtil;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

@Service
public class GitHubAnalyzer implements IGitAnalyzer {

    private final AppProperties appProperties;
    private final FsUtil fs;

    public GitHubAnalyzer(AppProperties appProperties, FsUtil fsUtil) {
        this.appProperties = appProperties;
        this.fs = fsUtil;
    }

    public void clone(RepositoryDto repositoryDto) {
        CloneCommand cloneCommand = Git.cloneRepository();
        cloneCommand.setURI("https://github.com/".concat(repositoryDto.fullName()));
        cloneCommand.setCredentialsProvider(
                new UsernamePasswordCredentialsProvider(appProperties.github().pat(), ""));
        cloneCommand.setDirectory(
                new File(appProperties.tmpDir() + "/" + APP_NAME + "/" + repositoryDto.name()));
        try {
            cloneCommand.call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    public void stat(RepositoryDto repositoryDto) {
        if (!fs.repoFolderExists(repositoryDto)) {
            clone(repositoryDto);
        }
    }
}

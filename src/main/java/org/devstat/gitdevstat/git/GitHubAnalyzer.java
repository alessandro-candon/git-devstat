/* OpenSource 2023 */
package org.devstat.gitdevstat.git;

import static org.devstat.gitdevstat.AppProperties.APP_NAME;

import java.io.File;
import java.io.IOException;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.utils.FsUtil;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

@Service
public class GitHubAnalyzer implements IGitAnalyzer {

    private final AppProperties appProperties;
    private final FsUtil fs;

    private final String workdir;

    public GitHubAnalyzer(AppProperties appProperties, FsUtil fsUtil) {
        this.appProperties = appProperties;
        this.fs = fsUtil;
        this.workdir = this.appProperties.tmpDir() + "/" + APP_NAME;
    }

    public void clone(RepositoryDto repositoryDto) {
        System.out.println("Cloning");
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
        System.out.println("End cloning");
    }

    public void stat(RepositoryDto repositoryDto) throws IOException, GitAPIException {
        if (!fs.repoFolderExists(repositoryDto)) {
            clone(repositoryDto);
        }
        System.out.println("Starting writing logs...");
        Repository repository = getExistentGitRepository(repositoryDto);
        Git git = new Git(repository);
        Iterable<RevCommit> commits = git.log().all().call();
        int count = 0;
        for (RevCommit commit : commits) {
            System.out.println("LogCommit: " + commit);
            count++;
            System.out.println(count);
        }
    }

    public Repository getExistentGitRepository(RepositoryDto repositoryDto) throws IOException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.setMustExist(true);
        repositoryBuilder.setGitDir(new File(this.workdir + "/" + repositoryDto.name() + "/.git"));
        return repositoryBuilder.build();
    }
}

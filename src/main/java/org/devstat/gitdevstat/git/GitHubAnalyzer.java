/* OpenSource 2023 */
package org.devstat.gitdevstat.git;

import static org.devstat.gitdevstat.AppProperties.APP_NAME;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.devstat.gitdevstat.git.dto.GitCommitResultDto;
import org.devstat.gitdevstat.utils.FsUtil;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class GitHubAnalyzer implements IGitAnalyzer {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(GitHubAnalyzer.class);

    private final AppProperties appProperties;
    private final FsUtil fs;

    private final NumStatReader numStatReader;

    public GitHubAnalyzer(AppProperties appProperties, FsUtil fsUtil, NumStatReader numStatReader) {
        this.appProperties = appProperties;
        this.fs = fsUtil;
        this.numStatReader = numStatReader;
    }

    /**
     * Clone a repository
     *
     * @param repositoryDto info about repo to be cloned
     * @return null if exception occurred, path of cloned repo otherwise
     */
    public String clone(RepositoryDto repositoryDto) {
        log.info(
                "Cloning repo name: {}, fullname: {}",
                repositoryDto.name(),
                repositoryDto.fullName());
        String storeDirPath = getStoreDirPath(repositoryDto);
        String gitPath = "github.com/".concat(repositoryDto.fullName());

        try {
            File storeDir = new File(storeDirPath);
            storeDir.mkdirs();
            int resCode =
                    execClone(
                            appProperties.github().pat(),
                            gitPath,
                            storeDir,
                            repositoryDto.isPrivate());
            log.debug("Clone finished with resultcode: {}", resCode);
        } catch (IOException | InterruptedException e) {
            log.error("Error during clone", e);
            return null;
        }

        return storeDirPath;
    }

    private int execClone(String pat, String repoPath, File destDir, boolean isPrivate)
            throws IOException, InterruptedException {
        final String[] realArgs = {
            "git", "clone", isPrivate ? "https://" + pat + "@" + repoPath : "https://" + repoPath,
        };
        var proc = Runtime.getRuntime().exec(realArgs, null, destDir.getParentFile());
        return proc.waitFor();
    }

    public Map<String, GitCommitResultDto> stat(RepositoryDto repositoryDto) throws IOException {
        String repoDirPath = getStoreDirPath(repositoryDto);
        if (!fs.repoFolderExists(repositoryDto)) {
            repoDirPath = clone(repositoryDto);
        }
        return numStatReader.getCommitStatistics(repoDirPath);
    }

    private String getStoreDirPath(RepositoryDto repositoryDto) {
        return appProperties.tmpDir() + "/" + APP_NAME + "/" + repositoryDto.name();
    }
}

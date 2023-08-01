/* OpenSource 2023 */
package org.devstat.gitdevstat.git;

import static org.devstat.gitdevstat.AppProperties.APP_NAME;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
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
    public String getLatestInfo(RepositoryDto repositoryDto) {
        log.info(
                "Updating repo name: {}, fullname: {}",
                repositoryDto.name(),
                repositoryDto.fullName());
        String storeDirPath = getStoreDirPath(appProperties, repositoryDto);
        String gitPath = "github.com/".concat(repositoryDto.fullName());
        File storeDir = new File(storeDirPath);

        try {
            File gitDir = new File(storeDirPath + "/.git");
            if (gitDir.isDirectory()
                    && Arrays.stream(gitDir.list()).anyMatch(p -> p.equals("HEAD"))) {
                int resCode = execPull(storeDir);
                log.debug(
                        "Pulling of {} finished with resultcode: {}",
                        repositoryDto.name(),
                        resCode);
            } else {
                storeDir.mkdirs();
                int resCode =
                        execClone(
                                appProperties.github().pat(),
                                gitPath,
                                storeDir,
                                repositoryDto.isPrivate());
                log.debug(
                        "Cloning of {} finished with resultcode: {}",
                        repositoryDto.name(),
                        resCode);
            }

        } catch (IOException | InterruptedException e) {
            log.error("Error repo update", e);
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

    private int execPull(File destDir) throws IOException, InterruptedException {
        final String[] realArgs = {"git", "pull"};
        var proc = Runtime.getRuntime().exec(realArgs, null, destDir);

        StringBuilder errStream = new StringBuilder();
        if (log.isDebugEnabled()) {
            BufferedReader input = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String line;
            while ((line = input.readLine()) != null) errStream.append(line);
        }

        int res = proc.waitFor();

        if (log.isDebugEnabled() && res != 0) log.error("{}", errStream.toString());

        return res;
    }

    public Map<String, GitCommitResultDto> stat(RepositoryDto repositoryDto) {
        String repoDirPath = getStoreDirPath(appProperties, repositoryDto);
        if (!fs.repoFolderExists(repositoryDto)) {
            repoDirPath = getLatestInfo(repositoryDto);
        }
        return numStatReader.getCommitStatistics(repoDirPath);
    }

    public static String getStoreDirPath(AppProperties appProperties, RepositoryDto repositoryDto) {
        return appProperties.cloneDir() + "/" + APP_NAME + "/" + repositoryDto.name();
    }
}

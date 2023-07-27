/* OpenSource 2023 */
package org.devstat.gitdevstat.utils;

import static org.devstat.gitdevstat.AppProperties.APP_NAME;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;
import org.springframework.stereotype.Service;

@Service
public class FsUtil {

    AppProperties appProperties;

    private String workdir;

    public FsUtil(AppProperties appProperties) {
        this.appProperties = appProperties;
        this.workdir = appProperties.cloneDir() + "/" + APP_NAME;
    }

    public void clearFolder() throws IOException {
        var f = new File(this.workdir);
        if (f.exists()) {
            FileUtils.deleteDirectory(f);
        }
    }

    public boolean repoFolderExists(RepositoryDto repositoryDto) {
        var f = new File(this.workdir + "/" + repositoryDto.name());
        return f.isDirectory();
    }
}

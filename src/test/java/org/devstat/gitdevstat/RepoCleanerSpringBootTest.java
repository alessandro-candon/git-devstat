/* OpenSource 2023 */
package org.devstat.gitdevstat;

import java.io.IOException;
import org.devstat.gitdevstat.utils.FsUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RepoCleanerSpringBootTest {

    @Autowired FsUtil fs;

    @BeforeEach
    void setUp() throws IOException {
        fs.clearFolder();
    }

    @AfterEach
    void tearDown() throws IOException {
        fs.clearFolder();
    }
}

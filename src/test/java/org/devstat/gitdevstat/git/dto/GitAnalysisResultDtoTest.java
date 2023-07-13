/* OpenSource 2023 */
package org.devstat.gitdevstat.git.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GitAnalysisResultDtoTest {

    @Test
    void testBuild() {
        var formattedCommit =
                "e3696af|Cesare Mauri|cesare.mauri@decathlon.com|cesare.mauri|Tue, 11 Jul 2023"
                    + " 12:16:00 +0200|1689070560|Cesare Mauri|cesare.mauri@decathlon.com|Tue, 11"
                    + " Jul 2023 12:16:00 +0200|1689070560|Add-netstat-in-main-app";
        var gitAnalysisResultDto = new GitCommitResultDto.Builder(formattedCommit).build();

        assertEquals(gitAnalysisResultDto.h(), "e3696af");
    }
}

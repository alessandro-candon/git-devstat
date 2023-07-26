/* OpenSource 2023 */
package org.devstat.gitdevstat.git.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GitAnalysisResultDtoTest {

    @Test
    void testBuild() {
        var formattedCommit =
                "e3696af|Cesare Mauri|cesare.mauri@test.com|cesare.mauri|Tue, 11 Jul 2023"
                        + " 12:16:00 +0200|1689070560|Cesare Mauri|cesare.mauri@test.com|Tue, 11"
                        + " Jul 2023 12:16:00 +0200|1689070560|Add-netstat-in-main-app";
        var gitAnalysisResultDto = new GitCommitResultDto.Builder(formattedCommit).build();

        assertEquals("e3696af", gitAnalysisResultDto.h());
        assertEquals(1689070560, gitAnalysisResultDto.at());
        assertEquals("Add-netstat-in-main-app", gitAnalysisResultDto.f());
    }
}

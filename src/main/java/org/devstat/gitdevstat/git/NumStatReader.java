/* OpenSource 2023 */
package org.devstat.gitdevstat.git;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.devstat.gitdevstat.git.dto.GitCommitResultDto;
import org.devstat.gitdevstat.git.dto.StatInfoWithPathDto;
import org.devstat.gitdevstat.git.utils.MutableInteger;
import org.devstat.gitdevstat.git.utils.RawParseUtils;
import org.devstat.gitdevstat.git.utils.TemporaryBuffer;
import org.springframework.stereotype.Service;

@Service
public class NumStatReader {
    private void onCommit(GitCommitResultDto gitAnalysisResultDto, byte[] buf) {
        final MutableInteger ptr = new MutableInteger();
        while (ptr.value < buf.length) {
            if (buf[ptr.value] == '\n') break;
            final int eol = RawParseUtils.nextLF(buf, ptr.value);
            final var filePath = RawParseUtils.decode(UTF_8, buf, ptr.value + 1, eol - 1);

            StatInfoWithPathDto i =
                    new StatInfoWithPathDto(
                            filePath,
                            RawParseUtils.parseBase10(buf, ptr.value, ptr),
                            RawParseUtils.parseBase10(buf, ptr.value + 1, ptr));
            gitAnalysisResultDto.statInfoDtoHashMap().put(filePath, i);
            ptr.value = eol;
        }
    }

    public Map<String, GitCommitResultDto> getStats(String repoPath) throws IOException {
        Process proc = prepareProcess(repoPath);
        Map<String, GitCommitResultDto> stats = new HashMap<>();
        try (BufferedReader in =
                new BufferedReader(new InputStreamReader(proc.getInputStream(), ISO_8859_1))) {
            String formattedLineOfCommit = null;
            GitCommitResultDto gitAnalysisResultDto = null;
            TemporaryBuffer buf = null;
            String line;

            while ((line = in.readLine()) != null) {
                if (line.startsWith("commit ")) {
                    if (buf != null) {
                        buf.close();
                        onCommit(gitAnalysisResultDto, buf.toByteArray());
                        buf.destroy();
                    }
                    formattedLineOfCommit = line.substring("commit ".length());

                    gitAnalysisResultDto =
                            new GitCommitResultDto.Builder(formattedLineOfCommit).build();

                    stats.put(gitAnalysisResultDto.h(), gitAnalysisResultDto);

                    buf = new TemporaryBuffer.LocalFile(null);
                } else if (buf != null) {
                    // go to next output of command
                    buf.write(line.getBytes(ISO_8859_1));
                    buf.write('\n');
                }
            }
        }
        return stats;
    }

    private Process prepareProcess(String repoPath) throws IOException {
        final String[] realArgs = {
            "git",
            "log",
            "--pretty=format:commit %h|%an|%ae|%al|%aD|%at|%cn|%ce|%cD|%ct|%f",
            "--numstat"
        };
        var proc = Runtime.getRuntime().exec(realArgs, null, new File(repoPath));
        proc.getOutputStream().close();
        proc.getErrorStream().close();
        return proc;
    }
}

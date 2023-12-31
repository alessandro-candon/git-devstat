/* OpenSource 2023 */
package org.devstat.gitdevstat.git;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.devstat.gitdevstat.git.dto.GitCommitResultDto;
import org.devstat.gitdevstat.git.dto.StatInfoWithPathDto;
import org.devstat.gitdevstat.git.utils.MutableInteger;
import org.devstat.gitdevstat.git.utils.RawParseUtils;
import org.devstat.gitdevstat.git.utils.TemporaryBuffer;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class NumStatReader {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(NumStatReader.class);

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

    public Map<String, GitCommitResultDto> getCommitStatistics(String repoPath) {
        return getCommitStatistics(repoPath, null, null);
    }

    public Map<String, GitCommitResultDto> getCommitStatistics(
            String repoPath, LocalDate from, LocalDate to) {

        if (repoPath == null) return Map.of();

        Map<String, GitCommitResultDto> stats = new HashMap<>();
        try {
            Process proc = prepareProcess(repoPath, from, to);
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
        } catch (IOException e) {
            log.error("unable to create statistics", e);
        }
        return stats;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-yy");

    private Process prepareProcess(String repoPath, LocalDate from, LocalDate to)
            throws IOException {
        List<String> args =
                new ArrayList() {
                    {
                        add("git");
                        add("log");
                        add("--pretty=format:commit %h|%an|%ae|%al|%aD|%at|%cn|%ce|%cD|%ct|%f");
                        add("--numstat");
                        if (from != null) add("--after=" + formatter.format(from));
                        if (from != to) add("--until=" + formatter.format(to));
                    }
                };

        var proc = Runtime.getRuntime().exec(args.toArray(new String[0]), null, new File(repoPath));
        proc.getOutputStream().close();
        proc.getErrorStream().close();
        return proc;
    }
}

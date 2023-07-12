/* OpenSource 2023 */
package org.devstat.gitdevstat.support;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.devstat.gitdevstat.git.dto.GitAnalysisResultDto;
import org.devstat.gitdevstat.git.dto.StatInfoDto;
import org.eclipse.jgit.util.MutableInteger;
import org.eclipse.jgit.util.RawParseUtils;
import org.eclipse.jgit.util.TemporaryBuffer;
import org.springframework.stereotype.Service;

@Service
public class NumStatReader {

    private final Map<String, Map<String, StatInfoDto>> stats = new HashMap<>();
    private Process proc;

    public void prepareProcess(String repoPath) throws IOException {
        final String[] realArgs = {
            "git",
            "log",
            "--pretty=format:commit %h|%an|%ae|%al|%aD|%at|%cn|%ce|%cD|%ct|%f",
            "--numstat"
        };
        proc = Runtime.getRuntime().exec(realArgs, null, new File(repoPath));
        proc.getOutputStream().close();
        proc.getErrorStream().close();
    }

    void onCommit(int call, String author, byte[] buf) {
        final HashMap<String, StatInfoDto> files = new HashMap<>();
        final MutableInteger ptr = new MutableInteger();
        while (ptr.value < buf.length) {
            if (buf[ptr.value] == '\n') break;
            StatInfoDto i =
                    new StatInfoDto(
                            RawParseUtils.parseBase10(buf, ptr.value, ptr),
                            RawParseUtils.parseBase10(buf, ptr.value + 1, ptr));
            final int eol = RawParseUtils.nextLF(buf, ptr.value);
            final String name =
                    call + ") " + RawParseUtils.decode(UTF_8, buf, ptr.value + 1, eol - 1);

            files.put(name, i);
            ptr.value = eol;
        }

        var oldFiles = stats.get(author);
        if (oldFiles != null) {
            files.putAll(oldFiles);
        }

        stats.put(author, files);
    }

    //    void onCommitWithObj(int call, GitAnalysisResultDto gitAnalysisResultDto, byte[] buf) {
    //        final HashMap<String, StatInfoWithPathDto> files = new HashMap<>();
    //        final MutableInteger ptr = new MutableInteger();
    //        while (ptr.value < buf.length) {
    //            if (buf[ptr.value] == '\n') break;
    //            final int eol = RawParseUtils.nextLF(buf, ptr.value);
    //            final var filePath = RawParseUtils.decode(UTF_8, buf, ptr.value + 1, eol - 1);
    //
    //            StatInfoWithPathDto i =
    //                    new StatInfoWithPathDto(
    //                            filePath,
    //                            RawParseUtils.parseBase10(buf, ptr.value, ptr),
    //                            RawParseUtils.parseBase10(buf, ptr.value + 1, ptr));
    //            files.put(filePath, i);
    //            ptr.value = eol;
    //        }
    //
    //        var oldFiles = stats.get(gitAnalysisResultDto.h());
    //        if (oldFiles != null) {
    //            files.putAll(oldFiles);
    //        }
    //
    //        stats.put(gitAnalysisResultDto.h(), files);
    //    }

    public Map<String, Map<String, StatInfoDto>> read() throws IOException {
        int call = 0;

        if (proc == null) {
            stats.clear();
            return new HashMap<>();
        }

        try (BufferedReader in =
                new BufferedReader(new InputStreamReader(proc.getInputStream(), ISO_8859_1))) {
            String formattedLineOfCommit = null;
            GitAnalysisResultDto gitAnalysisResultDto = null;
            TemporaryBuffer buf = null;
            String line;

            while ((line = in.readLine()) != null) {
                if (line.startsWith("commit ")) {
                    if (buf != null) {
                        buf.close();
                        onCommit(call++, formattedLineOfCommit, buf.toByteArray());
                        buf.destroy();
                    }
                    formattedLineOfCommit = line.substring("commit ".length());

                    gitAnalysisResultDto =
                            new GitAnalysisResultDto.Builder(formattedLineOfCommit).build();

                    buf = new TemporaryBuffer.LocalFile(null);
                } else if (buf != null) {
                    // go to next output of command
                    buf.write(line.getBytes(ISO_8859_1));
                    buf.write('\n');
                }
            }
        }
        proc = null;
        return stats;
    }

    public Map<String, StatInfoDto> aggregateByAuthor(Map<String, Map<String, StatInfoDto>> stats) {
        // @spotless:off
        return stats.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().values().stream()
                                .reduce((s1, s2) -> new StatInfoDto(s1.added() + s2.added(), s1.deleted() + s2.deleted()))
                                .orElse(new StatInfoDto(0, 0))));
        // @spotless:on
    }
}

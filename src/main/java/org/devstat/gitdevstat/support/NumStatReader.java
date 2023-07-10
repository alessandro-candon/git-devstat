/* OpenSource 2023 */
package org.devstat.gitdevstat.support;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.eclipse.jgit.util.MutableInteger;
import org.eclipse.jgit.util.RawParseUtils;
import org.eclipse.jgit.util.TemporaryBuffer;
import org.springframework.stereotype.Service;

@Service
public class NumStatReader {

    public record StatInfo(int added, int deleted) {}

    private final HashMap<String, HashMap<String, StatInfo>> stats = new HashMap<>();
    private Process proc;

    void prepareProcess(String repoPath) throws IOException {
        final String[] realArgs = {"git", "log", "--pretty=format:commit %H", "--numstat"};
        proc = Runtime.getRuntime().exec(realArgs, null, new File(repoPath));
        proc.getOutputStream().close();
        proc.getErrorStream().close();
    }

    void onCommit(String commitId, byte[] buf) {
        final HashMap<String, StatInfo> files = new HashMap<>();
        final MutableInteger ptr = new MutableInteger();
        while (ptr.value < buf.length) {
            if (buf[ptr.value] == '\n') break;
            StatInfo i =
                    new StatInfo(
                            RawParseUtils.parseBase10(buf, ptr.value, ptr),
                            RawParseUtils.parseBase10(buf, ptr.value + 1, ptr));
            final int eol = RawParseUtils.nextLF(buf, ptr.value);
            final String name = RawParseUtils.decode(UTF_8, buf, ptr.value + 1, eol - 1);
            files.put(name, i);
            ptr.value = eol;
        }
        stats.put(commitId, files);
    }

    HashMap<String, HashMap<String, StatInfo>> read() throws IOException {
        if (proc == null) {
            stats.clear();
            return null;
        }

        try (BufferedReader in =
                new BufferedReader(new InputStreamReader(proc.getInputStream(), ISO_8859_1))) {
            String commitId = null;
            TemporaryBuffer buf = null;
            String line;

            while ((line = in.readLine()) != null) {
                if (line.startsWith("commit ")) {
                    if (buf != null) {
                        buf.close();
                        onCommit(commitId, buf.toByteArray());
                        buf.destroy();
                    }
                    commitId = line.substring("commit ".length());
                    buf = new TemporaryBuffer.LocalFile(null);
                } else if (buf != null) {
                    buf.write(line.getBytes(ISO_8859_1));
                    buf.write('\n');
                }
            }
        }
        proc = null;
        return stats;
    }
}

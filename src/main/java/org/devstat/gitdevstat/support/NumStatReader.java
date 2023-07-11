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
import org.eclipse.jgit.util.MutableInteger;
import org.eclipse.jgit.util.RawParseUtils;
import org.eclipse.jgit.util.TemporaryBuffer;
import org.springframework.stereotype.Service;

@Service
public class NumStatReader {

    public record StatInfo(int added, int deleted) {}

    private final Map<String, Map<String, StatInfo>> stats = new HashMap<>();
    private Process proc;

    public void prepareProcess(String repoPath) throws IOException {
        final String[] realArgs = {"git", "log", "--pretty=format:commit %cn", "--numstat"};
        proc = Runtime.getRuntime().exec(realArgs, null, new File(repoPath));
        proc.getOutputStream().close();
        proc.getErrorStream().close();
    }

    void onCommit(int call, String author, byte[] buf) {
        final HashMap<String, StatInfo> files = new HashMap<>();
        final MutableInteger ptr = new MutableInteger();
        while (ptr.value < buf.length) {
            if (buf[ptr.value] == '\n') break;
            StatInfo i =
                    new StatInfo(
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

    public Map<String, Map<String, StatInfo>> read() throws IOException {
        int call = 0;

        if (proc == null) {
            stats.clear();
            return new HashMap<>();
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
                        onCommit(call++, commitId, buf.toByteArray());
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

    public Map<String, StatInfo> aggregateByAuthor(Map<String, Map<String, StatInfo>> stats) {
        Map<String, StatInfo> ret = new HashMap<>();

        for (String user : stats.keySet()) {
            for (StatInfo stat : stats.get(user).values()) {
                StatInfo aggStat = ret.get(user);
                if (aggStat == null) {
                    ret.put(user, stat);
                } else {
                    ret.put(
                            user,
                            new StatInfo(
                                    aggStat.added() + stat.added(),
                                    aggStat.deleted() + stat.deleted()));
                }
            }
        }
        return ret;
    }
}

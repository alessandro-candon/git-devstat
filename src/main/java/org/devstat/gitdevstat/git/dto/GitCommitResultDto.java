/* OpenSource 2023 */
package org.devstat.gitdevstat.git.dto;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

/**
 * This DTO respond to this fomat and informations format:%h|%an|%ae|%al|%aD|%at|%cn|%ce|%cD|%ct|%f
 *
 * @param h abbreviated commit hash
 * @param an author name
 * @param ae author email
 * @param al author email local-part (the part before the @ sign)
 * @param aD author date, RFC2822 style
 * @param at author date, UNIX timestamp
 * @param cn committer name
 * @param ce committer email
 * @param cD committer date, RFC2822 style
 * @param ct committer date, UNIX timestamp
 * @param f sanitized subject line, suitable for a filename
 */
public record GitCommitResultDto(
        String h,
        String an,
        String ae,
        String al,
        String aD,
        int at,
        String cn,
        String ce,
        String cD,
        int ct,
        String f,
        Map<String, StatInfoWithPathDto> statInfoDtoHashMap) {

    public static final class Builder {
        private static final Logger log =
                org.slf4j.LoggerFactory.getLogger(GitCommitResultDto.class);

        String formattedLog;

        public Builder(String formattedLog) {
            this.formattedLog = formattedLog;
        }

        public GitCommitResultDto build() {
            String[] tokens = formattedLog.split("\\|");
            try {
                int i = 0;
                return new GitCommitResultDto(
                        tokens[i++],
                        tokens[i++],
                        tokens[i++],
                        tokens[i++],
                        tokens[i++],
                        Integer.parseInt(tokens[i++]),
                        tokens[i++],
                        tokens[i++],
                        tokens[i++],
                        Integer.parseInt(tokens[i++]),
                        i == tokens.length - 1 ? tokens[i] : "",
                        new HashMap<>());
            } catch (Exception e) {
                log.error("Unable to parse line: {}", formattedLog);
                return null;
            }
        }
    }
}

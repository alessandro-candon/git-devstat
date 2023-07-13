/* OpenSource 2023 */
package org.devstat.gitdevstat.git.dto;

import java.util.HashMap;

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
        HashMap<String, StatInfoWithPathDto> statInfoDtoHashMap) {

    public static final class Builder {

        String formattedLog;

        public Builder(String formattedLog) {
            this.formattedLog = formattedLog;
        }

        public GitCommitResultDto build() {

            var explodedFormattedCommit = this.formattedLog.split("\\|");

            return new GitCommitResultDto(
                    explodedFormattedCommit[0],
                    explodedFormattedCommit[1],
                    explodedFormattedCommit[2],
                    explodedFormattedCommit[3],
                    explodedFormattedCommit[4],
                    Integer.parseInt(explodedFormattedCommit[5]),
                    explodedFormattedCommit[6],
                    explodedFormattedCommit[7],
                    explodedFormattedCommit[8],
                    Integer.parseInt(explodedFormattedCommit[9]),
                    explodedFormattedCommit[10],
                    new HashMap<>());
        }
    }
}

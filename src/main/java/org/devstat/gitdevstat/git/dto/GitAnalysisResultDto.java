/* OpenSource 2023 */
package org.devstat.gitdevstat.git.dto;

/**
 * This DTO respond to this fomat and informations
 * format:%h|%an|%aN|%ae|%aE|%al|%aL|%ad|%at|%cn|%ce|%cD|%ct|%f
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
public record GitAnalysisResultDto(
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
        String f) {}

/* OpenSource 2023 */
package org.devstat.gitdevstat.dto;

import java.util.Map;
import org.devstat.gitdevstat.git.dto.GitCommitResultDto;

public record JobResult(int code, Map<String, GitCommitResultDto> resultData) {}

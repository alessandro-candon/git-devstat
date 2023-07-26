/* OpenSource 2023 */
package org.devstat.gitdevstat.view.linesofcodebyauthor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.dto.GitRepositoryWithCommitResultDto;
import org.devstat.gitdevstat.git.dto.StatInfoWithPathDto;

public class LinesOfCodeByAuthorMerger {

    private AppProperties appProperties;

    public LinesOfCodeByAuthorMerger(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public Map<String, LinesOfCodeByAuthorDto> analyze(
            List<GitRepositoryWithCommitResultDto> gitRepositoryWithCommitResultDtoList) {
        var invertedAndDuplicatedAuthorAndId = new HashMap<String, String>();

        for (var authorEntrySet : appProperties.config().authorIds().entrySet()) {
            for (var authorValue : authorEntrySet.getValue()) {
                invertedAndDuplicatedAuthorAndId.put(authorValue, authorEntrySet.getKey());
            }
        }

        var linesOfCodeByAuthorDtoHashMap = new HashMap<String, LinesOfCodeByAuthorDto>();

        for (var gitRepository : gitRepositoryWithCommitResultDtoList) {
            for (var gitCommitEntry : gitRepository.resultData().entrySet()) {
                String authorCommitEmail = gitCommitEntry.getValue().ae();
                String authorId =
                        invertedAndDuplicatedAuthorAndId.getOrDefault(
                                authorCommitEmail, "nok_" + authorCommitEmail);

                var linesOfCodeByAuthorToFIll =
                        linesOfCodeByAuthorDtoHashMap.getOrDefault(
                                authorId, new LinesOfCodeByAuthorDto());

                var linesAddedThisCommit =
                        gitCommitEntry.getValue().statInfoDtoHashMap().values().stream()
                                .mapToInt(StatInfoWithPathDto::added)
                                .sum();

                var linesDeletedThisCommit =
                        gitCommitEntry.getValue().statInfoDtoHashMap().values().stream()
                                .mapToInt(StatInfoWithPathDto::deleted)
                                .sum();

                linesOfCodeByAuthorToFIll.addAddedLines(linesAddedThisCommit);
                linesOfCodeByAuthorToFIll.addDeletedLines(linesDeletedThisCommit);

                linesOfCodeByAuthorDtoHashMap.put(authorId, linesOfCodeByAuthorToFIll);
            }
        }
        return linesOfCodeByAuthorDtoHashMap;
    }
}

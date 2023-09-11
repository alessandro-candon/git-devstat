/* OpenSource 2023 */
package org.devstat.gitdevstat.view.linesofcodebyauthor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import org.devstat.gitdevstat.AppProperties;
import org.devstat.gitdevstat.dto.GitRepositoryWithCommitResultDto;
import org.devstat.gitdevstat.git.dto.GitCommitResultDto;
import org.devstat.gitdevstat.git.dto.StatInfoWithPathDto;
import org.slf4j.Logger;

public class LinesOfCodeByAuthorMerger {
    private static final Logger log =
            org.slf4j.LoggerFactory.getLogger(LinesOfCodeByAuthorMerger.class);

    private AppProperties appProperties;

    public LinesOfCodeByAuthorMerger(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public Map<String, LinesOfCodeByAuthorDto> analyze(
            List<GitRepositoryWithCommitResultDto> gitRepositoryWithCommitResultDtoList) {

        List<String> excludedFiles = Arrays.asList(appProperties.config().excludedFiles());
        log.info("Analyzing repo excluding: {}", excludedFiles);

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
                                authorId, new LinesOfCodeByAuthorDto(authorId));

                var linesAddedThisCommit =
                        countLines(gitCommitEntry, excludedFiles, StatInfoWithPathDto::added);

                var linesDeletedThisCommit =
                        countLines(gitCommitEntry, excludedFiles, StatInfoWithPathDto::deleted);

                linesOfCodeByAuthorToFIll.addAddedLines(linesAddedThisCommit);
                linesOfCodeByAuthorToFIll.addDeletedLines(linesDeletedThisCommit);

                linesOfCodeByAuthorDtoHashMap.put(authorId, linesOfCodeByAuthorToFIll);
            }
        }
        return linesOfCodeByAuthorDtoHashMap;
    }

    protected int countLines(
            Map.Entry<String, GitCommitResultDto> gitCommitEntry,
            List<String> excludedFiles,
            ToIntFunction<StatInfoWithPathDto> toIntFunctionSupplier) {
        return gitCommitEntry.getValue().statInfoDtoHashMap().values().stream()
                .peek(
                        f ->
                                log.trace(
                                        "File '{}' has been filtered? {}",
                                        f.filePath().split("\\t")[2],
                                        excludedFiles.stream()
                                                .anyMatch(
                                                        p ->
                                                                f.filePath()
                                                                        .split("\\t")[2]
                                                                        .matches(p))))
                .filter(
                        f ->
                                !excludedFiles.stream()
                                        .anyMatch(p -> f.filePath().split("\\t")[2].matches(p)))
                .mapToInt(toIntFunctionSupplier)
                .sum();
    }
}

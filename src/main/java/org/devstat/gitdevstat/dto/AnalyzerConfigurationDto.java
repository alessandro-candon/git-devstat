/* OpenSource 2023 */
package org.devstat.gitdevstat.dto;

import java.util.Map;

/**
 * @param authorIds : ALESSANDRO => [ alessandro@gmail.com, ale@gmail.com ... ]
 * @param excludedFiles : All files you want to exclude from total
 */
public class AnalyzerConfigurationDto {

    private final Map<String, String[]> authorIds;
    private final String[] excludedFiles;

    private TimeFrameDto timeFrameDto = null;

    public AnalyzerConfigurationDto(
            Map<String, String[]> authorIds, String[] excludedFiles, TimeFrameDto timeFrameDto) {
        this.authorIds = authorIds;
        this.excludedFiles = excludedFiles;
        this.timeFrameDto = timeFrameDto;
    }

    public AnalyzerConfigurationDto(Map<String, String[]> authorIds, String[] excludedFiles) {
        this.authorIds = authorIds;
        this.excludedFiles = excludedFiles;
    }

    public Map<String, String[]> getAuthorIds() {
        return authorIds;
    }

    public String[] getExcludedFiles() {
        return excludedFiles;
    }

    public TimeFrameDto getTimeFrameDto() {
        return timeFrameDto;
    }
}

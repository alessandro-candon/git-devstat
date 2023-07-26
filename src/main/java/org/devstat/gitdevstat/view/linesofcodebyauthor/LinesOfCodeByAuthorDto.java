/* OpenSource 2023 */
package org.devstat.gitdevstat.view.linesofcodebyauthor;

public class LinesOfCodeByAuthorDto {
    int added = 0;

    int deleted = 0;

    public LinesOfCodeByAuthorDto() {}

    public LinesOfCodeByAuthorDto(int added, int deleted) {
        this.added = added;
        this.deleted = deleted;
    }

    public int getAdded() {
        return added;
    }

    public int getDeleted() {
        return deleted;
    }

    public void addAddedLines(int added) {
        this.added += added;
    }

    public void addDeletedLines(int deleted) {
        this.deleted += deleted;
    }
}

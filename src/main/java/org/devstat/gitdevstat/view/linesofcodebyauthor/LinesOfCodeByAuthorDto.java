/* OpenSource 2023 */
package org.devstat.gitdevstat.view.linesofcodebyauthor;

public class LinesOfCodeByAuthorDto {

    String authorId;
    int added = 0;

    int deleted = 0;

    public LinesOfCodeByAuthorDto() {}

    public LinesOfCodeByAuthorDto(String authorId) {
        this.authorId = authorId;
    }

    public LinesOfCodeByAuthorDto(String authorId, int added, int deleted) {
        this.authorId = authorId;
        this.added = added;
        this.deleted = deleted;
    }

    public String getAuthorId() {
        return authorId;
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

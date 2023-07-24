/* OpenSource 2023 */
package org.devstat.gitdevstat.view.linesofcodebyauthor;

public class LinesOfCodeByAuthorDto {
    int added;

    int deleted;

    public LinesOfCodeByAuthorDto() {}

    public void addAddedLines(int added) {
        this.added += added;
    }

    public void addDeletedLines(int deleted) {
        this.deleted += deleted;
    }
}

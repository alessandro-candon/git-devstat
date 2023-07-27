/* OpenSource 2023 */
package org.devstat.gitdevstat.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;
import org.devstat.gitdevstat.view.linesofcodebyauthor.LinesOfCodeByAuthorDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Export util test")
class ExportUtilTest {

    @Autowired private ExportUtil exportUtil;

    @Test
    void testCsvExporting() throws Exception {
        // Writer writer = Files.newBufferedWriter(Paths.get("/tmp/test.csv"));
        Writer strWriter = new StringWriter();

        String exceptRes =
                """
                        "AUTHORID","ADDED","DELETED"
                        "Alexander","1","2"
                        "ZZZ","0","0"
                        "Cèsar","2147483647","0"
                        """;

        Set<LinesOfCodeByAuthorDto> data =
                Set.of(
                        new LinesOfCodeByAuthorDto("Alexander", 1, 2),
                        new LinesOfCodeByAuthorDto("ZZZ", 0, 0),
                        new LinesOfCodeByAuthorDto("Cèsar", Integer.MAX_VALUE, 0));

        String[] order = new String[] {"AUTHORID", "ADDED", "DELETED"};

        exportUtil.serializeToCsv(strWriter, data, order);

        assertThat(strWriter).isNotNull();
        assertThat(strWriter.toString()).isEqualTo(exceptRes);
    }
}

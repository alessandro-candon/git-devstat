/* OpenSource 2023 */
package org.devstat.gitdevstat.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;
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
                        "Cèsar","2147483647","0"
                        "ZZZ","0","0"
                        "Alexander","1","2"
                        """;

        Map<String, LinesOfCodeByAuthorDto> data =
                Map.of(
                        "Alexander", new LinesOfCodeByAuthorDto("Alexander", 1, 2),
                        "ZZZ", new LinesOfCodeByAuthorDto("ZZZ", 0, 0),
                        "Cèsar", new LinesOfCodeByAuthorDto("Cèsar", Integer.MAX_VALUE, 0));

        String[] order = new String[] {"AUTHORID", "ADDED", "DELETED"};

        exportUtil.serializeToCsv(strWriter, data.values(), order);

        assertThat(strWriter).isNotNull();
        Arrays.stream(exceptRes.split("\n"))
                .forEach(row -> assertThat(strWriter.toString()).contains(row));
    }
}

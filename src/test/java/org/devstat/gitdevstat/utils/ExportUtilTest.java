/* OpenSource 2023 */
package org.devstat.gitdevstat.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;
import java.io.Writer;
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
                Alexander,1,2
                Cèsar,2147483647,0
                ZZZ,0,0
                """;

        Map<String, LinesOfCodeByAuthorDto> data =
                Map.of(
                        "Alexander", new LinesOfCodeByAuthorDto(1, 2),
                        "ZZZ", new LinesOfCodeByAuthorDto(0, 0),
                        "Cèsar", new LinesOfCodeByAuthorDto(Integer.MAX_VALUE, 0));

        exportUtil.serializeToCsv(strWriter, data);

        assertThat(strWriter).isNotNull();
        assertThat(strWriter.toString()).isEqualTo(exceptRes);
    }
}

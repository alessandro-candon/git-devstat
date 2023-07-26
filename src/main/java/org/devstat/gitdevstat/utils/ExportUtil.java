/* OpenSource 2023 */
package org.devstat.gitdevstat.utils;

import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.devstat.gitdevstat.view.linesofcodebyauthor.LinesOfCodeByAuthorDto;
import org.springframework.stereotype.Service;

@Service
public class ExportUtil {
    public ExportUtil() {}

    public void serializeToCsv(Writer writer, Map<String, LinesOfCodeByAuthorDto> data) {
        TreeMap<String, LinesOfCodeByAuthorDto> sorted = new TreeMap<>();
        sorted.putAll(data);

        List<String[]> sData = new ArrayList<>(sorted.size());
        for (Map.Entry<String, LinesOfCodeByAuthorDto> entry : sorted.entrySet()) {
            sData.add(
                    new String[] {
                        entry.getKey(),
                        Integer.toString(entry.getValue().getAdded()),
                        Integer.toString(entry.getValue().getDeleted())
                    });
        }

        ICSVWriter csvWriter =
                new CSVWriterBuilder(writer)
                        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                        .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                        .build();

        csvWriter.writeAll(sData);
    }
}

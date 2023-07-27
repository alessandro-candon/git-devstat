/* OpenSource 2023 */
package org.devstat.gitdevstat.utils;

import com.opencsv.CSVWriter;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.bean.comparator.LiteralComparator;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.Writer;
import java.util.*;
import org.devstat.gitdevstat.view.linesofcodebyauthor.LinesOfCodeByAuthorDto;
import org.springframework.stereotype.Service;

@Service
public class ExportUtil {
    public void serializeToCsv(Writer writer, Collection coll, String[] literalOrder)
            throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

        List data = new ArrayList(coll);

        HeaderColumnNameMappingStrategy<LinesOfCodeByAuthorDto> strategy =
                new HeaderColumnNameMappingStrategyBuilder<LinesOfCodeByAuthorDto>().build();
        strategy.setType(LinesOfCodeByAuthorDto.class);
        strategy.setColumnOrderOnWrite(new LiteralComparator(literalOrder));

        StatefulBeanToCsv beanToCsv =
                new StatefulBeanToCsvBuilder(writer)
                        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                        .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                        .withOrderedResults(true)
                        .withMappingStrategy(strategy)
                        .build();
        beanToCsv.write(data);
    }
}

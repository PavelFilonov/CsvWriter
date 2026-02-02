package org.writer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.writer.CsvColumn;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Student {

    @CsvColumn(name = "name", order = 1)
    private String name;

    @CsvColumn(name = "score", order = 2)
    private List<String> score;
}
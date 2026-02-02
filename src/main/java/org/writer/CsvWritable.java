package org.writer;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.util.stream.Collectors.joining;

/**
 * Реализация Writable, сохраняющая список объектов в CSV файл
 */
public class CsvWritable implements Writable {

    private final String separator;

    /**
     * Создаёт CsvWritable с разделителем по умолчанию (запятая).
     */
    public CsvWritable() {
        this(",");
    }

    /**
     * Создаёт CsvWritable с указанным разделителем.
     *
     * @param separator строка-разделитель
     */
    public CsvWritable(String separator) {
        if (StringUtils.isEmpty(separator)) {
            throw new IllegalArgumentException("Сепаратор не заполнен");
        }
        this.separator = separator;
    }

    /**
     * Сохраняет список объектов в CSV файл
     *
     * @param data     список объектов одного типа
     * @param fileName путь к файлу (создаётся/перезаписывается)
     */
    @Override
    public void writeToFile(List<?> data, String fileName) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Данные не заполнены");
        }
        Class<?> clazz = data.get(0).getClass();
        List<Field> columns = getAnnotatedFields(clazz);
        List<String> header = columns.stream().map(f -> {
            CsvColumn ann = f.getAnnotation(CsvColumn.class);
            return ann.name().isEmpty()
                    ? f.getName()
                    : ann.name();
        }).toList();
        Path path = Path.of(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path, CREATE, TRUNCATE_EXISTING)) {
            writer.write(String.join(separator, header));
            writer.newLine();
            for (Object obj : data) {
                List<String> row = new ArrayList<>();
                for (Field field : columns) {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    row.add(escapeCsv(formatValue(value)));
                }
                writer.write(String.join(separator, row));
                writer.newLine();
            }
        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException("Ошибка записи CSV файла", e);
        }
    }

    private List<Field> getAnnotatedFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        return Arrays.stream(fields)
                .filter(f -> f.isAnnotationPresent(CsvColumn.class))
                .sorted(Comparator.comparingInt(f -> f.getAnnotation(CsvColumn.class).order()))
                .toList();
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Enum<?>) {
            return ((Enum<?>) value).name();
        }
        if (value instanceof Collection<?>) {
            return ((Collection<?>) value).stream()
                    .map(Objects::toString)
                    .collect(joining(";"));
        }
        return value.toString();
    }

    private String escapeCsv(String field) {
        if (StringUtils.isEmpty(field)) {
            return "";
        }
        boolean needQuotes = field.contains(",")
                || field.contains("\"")
                || field.contains("\n")
                || field.contains("\r")
                || field.contains(";");
        String escaped = field.replace("\"", "\"\"");
        return needQuotes
                ? "\"" + escaped + "\""
                : escaped;
    }
}

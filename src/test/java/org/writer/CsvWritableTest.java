package org.writer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.writer.model.Months;
import org.writer.model.Person;
import org.writer.model.Student;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CsvWritableTest {

    private Writable writable;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        writable = new CsvWritable();
    }

    @Test
    void writesPersonCsv() throws IOException {
        Person p1 = Person.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .dayOfBirth(5)
                .monthOfBirth(Months.MARCH)
                .yearOfBirth(1990)
                .build();
        Person p2 = Person.builder()
                .firstName("Anna")
                .lastName("Ivanova")
                .dayOfBirth(12)
                .monthOfBirth(Months.DECEMBER)
                .yearOfBirth(1985)
                .build();
        Path file = tempDir.resolve("persons.csv");

        writable.writeToFile(List.of(p1, p2), file.toString());

        String content = Files.readString(file);
        assertThat(content).contains("first_name,last_name,day_of_birth,month_of_birth,year_of_birth");
        assertThat(content).contains("Ivan,Petrov,5,MARCH,1990");
        assertThat(content).contains("Anna,Ivanova,12,DECEMBER,1985");
    }

    @Test
    void writesStudentCsvWithListField() throws IOException {
        Student s = Student.builder().name("Sidor Sidorov").score(List.of("80", "90", "75")).build();
        Path file = tempDir.resolve("students.csv");

        writable.writeToFile(List.of(s), file.toString());

        String content = Files.readString(file);
        assertThat(content).contains("name,score");
        assertThat(content).contains("Sidor Sidorov");
        assertThat(content).contains("80;90;75");
    }

}
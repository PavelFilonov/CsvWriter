package org.writer;

import com.github.javafaker.Faker;
import org.writer.model.Months;
import org.writer.model.Person;
import org.writer.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("ru"));
        List<Person> people = IntStream.range(0, 10)
                .mapToObj(i -> Person.builder()
                        .firstName(faker.name().firstName())
                        .lastName(faker.name().lastName())
                        .dayOfBirth(faker.number().numberBetween(1, 28))
                        .monthOfBirth(Months.values()[faker.number().numberBetween(0, Months.values().length)])
                        .yearOfBirth(faker.number().numberBetween(1970, 2010))
                        .build())
                .toList();
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<String> scores = IntStream.range(0, 4)
                    .mapToObj(j -> String.valueOf(faker.number().numberBetween(50, 100)))
                    .toList();
            students.add(Student.builder()
                    .name(faker.name().fullName())
                    .score(scores)
                    .build());
        }
        Writable writer = new CsvWritable();
        writer.writeToFile(people, "people.csv");
        writer.writeToFile(students, "students.csv");
        System.out.println("CSV файлы (people.csv, students.csv) успешно заполнены");
    }
}
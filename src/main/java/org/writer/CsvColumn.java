package org.writer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для пометки полей, которые должны попадать в CSV. * Поле order используется для задания порядка колонок в выходном файле.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvColumn {

    String name() default "";

    int order() default Integer.MAX_VALUE;

}

package mg.itu.prom16.Annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {
    String message() default "Ne doit pas etre null";
}

package mg.itu.prom16.Annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Min {
    public int value();
    public String message() default "Valeur trop petite";
}

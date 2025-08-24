package mg.itu.prom16.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import mg.itu.prom16.Annotation.Max;
import mg.itu.prom16.Annotation.Min;
import mg.itu.prom16.Annotation.NotNull;


public class Validation {

    public static void valider(Parameter parameter, Object object) throws Exception {
        // min
        if (parameter.isAnnotationPresent(Min.class) && object instanceof Integer) {
            Min min = parameter.getAnnotation(Min.class);
            if ((Integer) object < min.value()) {
                throw new Exception("Validation Error: " + min.message() + " (Param : " + parameter.getName() + ")");
            }
        }

        // notNull
        if (parameter.isAnnotationPresent(NotNull.class) && (object == null || object.toString().isEmpty())) {
            NotNull notNull = parameter.getAnnotation(NotNull.class);
            throw new Exception("Validation Error: " + notNull.message() + " (Param : " + parameter.getName() + ")");
        }

        // max
        if (parameter.isAnnotationPresent(Max.class) && object instanceof Integer) {
            Max max = parameter.getAnnotation(Max.class);
            if ((Integer) object > max.value()) {
                throw new Exception("Validation Error: " + max.message() + " (Param : " + parameter.getName() + ")");
            }
        }

    }

    public static void validerObjet(Object object, Field field) throws Exception{
        // notNull
        if (field.isAnnotationPresent(NotNull.class)) {
            Object value = field.get(object);
            if (value == null) {
                NotNull annotation = field.getAnnotation(NotNull.class);
                throw new Exception("Validation Error: " + annotation.message() + " (Champ : " + field.getName() + ")");
            }
        }

        // min
        if (field.isAnnotationPresent(Min.class)) {
            Min annotation = field.getAnnotation(Min.class);
            Object value = field.get(object);
            if (value instanceof Integer && (Integer) value < annotation.value()) {
                throw new Exception("Validation Error: " + annotation.message() + " (Champ : " + field.getName() + ")");
            }
        }

        // max
        if (field.isAnnotationPresent(Max.class)) {
            Max annotation = field.getAnnotation(Max.class);
            Object value = field.get(object);
            if (value instanceof Integer && (Integer) value > annotation.value()) {
                throw new Exception("Validation Error: " + annotation.message() + " (Champ : " + field.getName() + ")");
            }
        }
    }


    public static void validerObject(Object object, Field field) throws Exception{
        // notNull
        if (field.isAnnotationPresent(NotNull.class)) {
            if (object == null) {
                NotNull annotation = field.getAnnotation(NotNull.class);
                throw new Exception("Validation Error: " + annotation.message() + " (Champ : " + field.getName() + ")");
            }
        }

        // min
        if (field.isAnnotationPresent(Min.class)) {
            Min annotation = field.getAnnotation(Min.class);
            if (object instanceof Integer && (Integer) object < annotation.value()) {
                throw new Exception("Validation Error: " + annotation.message() + " (Champ : " + field.getName() + ")");
            }
        }

        // max
        if (field.isAnnotationPresent(Max.class)) {
            Max annotation = field.getAnnotation(Max.class);
            if (object instanceof Integer && (Integer) object > annotation.value()) {
                throw new Exception("Validation Error: " + annotation.message() + " (Champ : " + field.getName() + ")");
            }
        }
    }

}

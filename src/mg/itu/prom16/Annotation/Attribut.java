package mg.itu.prom16.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)  // L'annotation sera disponible à l'exécution
@Target(ElementType.FIELD)  // L'annotation peut être appliquée aux champs
public @interface Attribut {
    String value();  // Déclare un élément d'annotation
}

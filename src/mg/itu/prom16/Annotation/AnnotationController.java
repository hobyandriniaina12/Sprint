package mg.itu.prom16.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Déclare l'annotation
// UTILISATION : Sprint1
@Retention(RetentionPolicy.RUNTIME) // Spécifie que l'annotation sera disponible à l'exécution
@Target(ElementType.TYPE) // Spécifie que l'annotation peut être appliquée aux classes et interfaces
public @interface AnnotationController {

}


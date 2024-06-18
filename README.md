## spirnt1
Sprint1 Affiche une liste des controllers annotees par AnnotationController
Creer des controllers de test.
Modification dans web.xml :
    <servlet>
        <servlet-name>FrontController</servlet-name>
        <servlet-class>mg.itu.prom16.controllers.FrontController</servlet-class>
        <init-param>
            <param-name>ControllerPackage</param-name>
            <param-value>controller</param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>FrontController</servlet-name>
        <url-pattern>/sprint0</url-pattern>
    </servlet-mapping>



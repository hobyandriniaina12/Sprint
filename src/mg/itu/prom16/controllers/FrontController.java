package mg.itu.prom16.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.Annotation.AnnotationController;
import mg.itu.prom16.Annotation.GET;
import mg.itu.prom16.Util.Mapping;

public class FrontController extends HttpServlet
{
    String controllerPackage;   //spirnt1
    List<String> listController;    //sprint1
    Map<String, Mapping> map = new HashMap<>();
    String servletPath = "";
    @Override
    public void init() throws ServletException {
        super.init();
        controllerPackage = getServletConfig().getInitParameter("ControllerPackage");
        listController = new ArrayList<>();
        try {
            scanner();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String urlActuel = request.getRequestURL().toString();
        String urlServlet = getUrlPrincipale(request);
        String ifValue = urlActuel.replaceFirst(urlServlet,"");
        Mapping mapping = map.get(ifValue);
        if (mapping != null) {
            out.println("CHemin url : " + urlActuel);
            out.println("Nom classe : " + mapping.getClassName());
            out.println("Nom methode : "+mapping.getMethodName());
        } else {
            out.println("Il n'y a pas de methode associee a ce chemin");
        }
        
    }

    public void scanner() throws Exception
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = controllerPackage.replace('.', '/');
        URL url = classLoader.getResource(path);
        String urlDecode = URLDecoder.decode(url.getFile(),"UTF-8");
        File directory = new File(urlDecode);
        if (directory.exists() && directory.isDirectory())
        {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".class")) {
                        String className = controllerPackage + '.' + file.getName().substring(0, file.getName().lastIndexOf('.'));
                        if (isAnnoted(className, AnnotationController.class)) {
                            listController.add(className);
                        }
                    }
                }
            }
        }
        // mitety anle controleur de mijery ny methodeny
        for (String controller : listController) {
            // Charger la classe à partir du fichier
            Class<?> clazz = Class.forName(controller); // Supprime l'extension.class
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(GET.class)) {
                    String nomController = clazz.getName();
                    String nomMethode = method.getName();
                    String urlValue = method.getAnnotation(GET.class).value();
                    Mapping mapping = new Mapping(nomController,nomMethode);
                    map.put(urlValue, mapping);
                }
            }
        }

    }
    private static boolean isAnnoted(String className, Class<? extends Annotation> annotation) {
        try {
            Class<?> cls = Class.forName(className);
            return cls.isAnnotationPresent(annotation);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected String getUrlPrincipale(HttpServletRequest request)
    {
        String scheme = request.getScheme();             // http ou https
        String serverName = request.getServerName();     // Nom du serveur (domaine)
        int serverPort = request.getServerPort();        // Port du serveur
        String contextPath = request.getContextPath();   // Chemin du contexte
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
         
        if (serverPort != 80 && serverPort != 443) {
            url.append(":").append(serverPort);
        }
        url.append(contextPath).append(servletPath);
        return url.toString();
    }

}

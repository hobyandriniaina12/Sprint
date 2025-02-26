package mg.itu.prom16.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
import mg.itu.prom16.Annotation.Attribut;
import mg.itu.prom16.Annotation.GET;
import mg.itu.prom16.Annotation.POST;
import mg.itu.prom16.Annotation.Param;
import mg.itu.prom16.Annotation.ResponseBody;
import mg.itu.prom16.Annotation.RestController;
import mg.itu.prom16.Util.Mapping;
import mg.itu.prom16.Util.ModelView;
import mg.itu.prom16.Util.Session;

import com.google.gson.Gson;


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
            throw new ServletException("Erreur de mapping des URLs : " + e.getMessage(), e);
        }

    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp,"GET");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp,"POST");
        } catch (Exception e) {
            throw new ServletException(e);
            
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response,String verb) throws Exception
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String urlActuel = request.getRequestURL().toString();
        String urlServlet = getUrlPrincipale(request);
        String ifValue = urlActuel.replaceFirst(urlServlet,"");
        Mapping mapping = map.get(ifValue);

        if (mapping != null) {
            Class<?> cls = Class.forName(mapping.getClassName());
            Method method = mapping.getMethod(verb);
            if (method == null) {
                // throw new ServletException("pas de methode "+verb+" pour cette url");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "pas de methode "+verb+" pour cette url");
                return;
            }
            boolean existResponseBody = false;
            if (method.isAnnotationPresent(ResponseBody.class)) {
                existResponseBody = true;
            }
            Object o = cls.getDeclaredConstructor().newInstance();
            Object objectType;
            // verifier=na oe misy argument ve le fonction
            if (method.getParameterCount() != 0) {
                    objectType = invokeMethodeWithParametres(method, request, o);
            }
            else{
                    objectType = method.invoke(o);
            }
            
            // verifiena oe string sa ModelView
            if (objectType instanceof ModelView) {
                ModelView modelvView = (ModelView) objectType;
                String url = modelvView.getUrl();
                HashMap<String,Object> data = modelvView.getData();
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }
                if (cls.isAnnotationPresent(RestController.class) && existResponseBody == false) {
                    out.println("Json: "+ modelvView.toJson());
                }
                else{
                    request.getRequestDispatcher(url).forward(request, response);
                }
            }
            else if(objectType instanceof String){
                out.println("CHemin url : " + urlActuel);
                out.println("Nom classe : " + mapping.getClassName());
                out.println("Nom methode : "+mapping.getMethodName());
                if (cls.isAnnotationPresent(RestController.class) && existResponseBody == false) {
                    Gson gson = new Gson();
                    out.println("Json: "+ gson.toJson(objectType));
                }
                else{
                    out.println("Retour : "+ objectType);
                }
                
            }
            else{
                if (cls.isAnnotationPresent(RestController.class) && existResponseBody == false) {
                    Gson gson = new Gson();
                    out.println("Json: "+ gson.toJson(objectType));
                }
                else{
                    throw new ServletException("Type de retour non reconnu");    
                }
            }
            
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "L'url n existe pas");
            return;
        }
        
    }

    public void scanner() throws Exception
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = controllerPackage.replace('.', '/');
        URL url = classLoader.getResource(path);
        if (url != null) {
            String urlDecode = URLDecoder.decode(url.getFile(),"UTF-8");
            File directory = new File(urlDecode);
            if (directory.exists() && directory.isDirectory())
            {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".class")) {
                            String className = controllerPackage + '.' + file.getName().substring(0, file.getName().lastIndexOf('.'));
                            if (isAnnoted(className, AnnotationController.class) || isAnnoted(className, RestController.class)) {
                                listController.add(className);
                            }
                        }
                    }
                }
                else{
                    throw new ServletException("Package des controllers vide");
                }
                // mitety anle controleur de mijery ny methodeny
                for (String controller : listController) {
                    // Charger la classe à partir du fichier
                    Class<?> clazz = Class.forName(controller); // Supprime l'extension.class
                    Method[] methods = clazz.getDeclaredMethods();
                    int l = 0;
                    for (Method method : methods) {
                        l++;
                        String nomController = clazz.getName();
                        String nomMethode = method.getName();
                        if (method.isAnnotationPresent(GET.class)) {
                            String urlValue = method.getAnnotation(GET.class).value(); // le url anle get
                            if (map.containsKey(urlValue)) {
                                if (!map.get(urlValue).haveGet()) {
                                    map.get(urlValue).addMethod("GET", method);
                                }
                                else{
                                    throw new ServletException("Deux fonctions ont le même URL GET : " + urlValue);                                }
                            }
                            else{
                                Mapping mapping = new Mapping(nomController,nomMethode);
                                mapping.addMethod("GET", method);    
                                map.put(urlValue, mapping);
                            }
                        }
                        else if (method.isAnnotationPresent(POST.class)) {
                            String urlValue = method.getAnnotation(POST.class).value(); // le url anle get
                            if (map.containsKey(urlValue)) {
                                if (!map.get(urlValue).havePost()) {
                                    map.get(urlValue).addMethod("POST", method);
                                }
                                else{
                                    throw new ServletException("Deux fonctions ont le même URL POST : " + urlValue);
                                }
                            }
                            else{
                                Mapping mapping = new Mapping(nomController,nomMethode);
                                mapping.addMethod("POST", method);    
                                map.put(urlValue, mapping);
                            }
                        }
                    }
                }
            }
        }
        else{
            throw new ServletException("package vide ou  non existant");
        }
    }

    private static void verifyVerb(String url,Map<String, Mapping> map)
    {
        if (map.containsKey(url)) {
            Mapping mapping = map.get(url);

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

    public Object invokeMethodeWithParametres(Method method, HttpServletRequest request,Object o) throws Exception {
        Parameter[] parameters = method.getParameters();
                Object[] arguments = new Object[parameters.length];

                for (int i = 0; i < parameters.length; i++) {
                    if (parameters[i].getType().isAssignableFrom(Session.class)) {  
                        arguments[i] = new Session(request);
                        continue;
                    }
                    Param paramAnnotation = parameters[i].getAnnotation(Param.class);
                    Class<?> type = parameters[i].getType();
                    if (!type.equals(String.class)) {
                        Object object = type.getDeclaredConstructor().newInstance();
                        Field[] fields = object.getClass().getDeclaredFields(); 
                        for (Field field : fields) {
                            String name = field.getDeclaredAnnotation(Attribut.class).value();
                            field.setAccessible(true);
                            if (field.getType().equals(Integer.class) ||  field.getType().equals(int.class)) {
                                field.set(object, Integer.parseInt(request.getParameter(paramAnnotation.name() + "."+ name)));
                                
                            }
                            else if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
                                field.set(object, Double.parseDouble(request.getParameter(paramAnnotation.name() + "."+ name)));
                            }
                            else {
                                field.set(object, request.getParameter(paramAnnotation.name() + "."+ name));
                            }
                            field.setAccessible(false);
                        }
                        arguments[i] = object;
                    }
                    else{
                        if (paramAnnotation != null) {
                            String paramName = paramAnnotation.name();
                            arguments[i] = request.getParameter(paramName);
                        }
                        else{
                            arguments[i] = request.getParameter(parameters[i].getName());
                        }
                    }
                    
                }
                return method.invoke(o, arguments);
                
    }
}

package mg.itu.prom16.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.Annotation.AnnotationController;

import java.io.File;
import java.util.List;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;

public class FrontController extends HttpServlet
{
    String controllerPackage;
    List<String> listController;

    @Override
    public void init() throws ServletException {
        super.init();
        controllerPackage = getServletConfig().getInitParameter("ControllerPackage");
        listController = new ArrayList<>();
        try {
            scanner();
        } catch (IOException e) {
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
        for (String controller : listController) {
            out.println(controller);
        }
    }   

    public void scanner() throws IOException
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
}

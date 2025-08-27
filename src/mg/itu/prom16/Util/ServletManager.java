package mg.itu.prom16.Util;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@SuppressWarnings("deprecation")
public class ServletManager {
    public ArrayList<Class<?>> getControllerClasses(String packageName) throws Exception {
        ArrayList<Class<?>> classes = Utils.getClasses(packageName);
        ArrayList<Class<?>> result = new ArrayList<Class<?>>();
        
        for(Class<?> classe : classes) {
            if (classe.isAnnotationPresent(Controller.class)) {
                result.add(classe);
            } 
        }
        if (result.size() <= 0) {
            throw new PackageException(packageName);
        }
        return result;
    }

    public HashMap<String,Mapping> getControllerMethod(ArrayList<Class<?>> classes) throws Exception {
        HashMap<String,Mapping> result = new HashMap<>();
        if (classes != null) {
            for(Class<?> classe : classes) {
                ArrayList<Method> methods = Utils.getListMethod(classe);
                for (Method method : methods) {
                    if (method.isAnnotationPresent(Url.class)) {
                        String verb = Utils.getVerb(method);

                        String url = ((Url) method.getAnnotation(Url.class)).value();
                        Mapping map = result.get(url);
                        
                        if (map == null) {
                            Mapping mapping = new Mapping(classe.getSimpleName());
                            mapping.addVerbAction(new VerbAction(method.getName(), verb));
                            result.put(url, mapping);
                        } else {
                            VerbAction verbAction = new VerbAction(method.getName(), verb);
                            if (!Utils.isVerbExistInMapping(map, verbAction)) {
                                map.addVerbAction(verbAction);
                            } else {
                                throw new DuplicateException();
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public void executeMethodController(String url, VerbAction verbAction,HttpServletRequest request, HttpServletResponse response, String packageName, HashMap<String,Mapping> controllerAndMethod) throws Exception {
        PrintWriter out = response.getWriter();
        Mapping map = new Mapping().getUrl(controllerAndMethod, url);
        ModelView model = new ModelView();

        String className = map.getClassName();
        String methodName = verbAction.getMethod();
        String classPath = packageName + "." + className;
        
        Class<?> controllerClass = Class.forName(classPath);
        Method controllerMethod = Utils.getMethod(controllerClass, methodName);

        if (controllerMethod.getReturnType() == String.class || controllerMethod.getReturnType() == ModelView.class) {
            Object ctrlObj = controllerClass.newInstance(); 
            
            if (controllerMethod.getReturnType() == String.class) {
                String methodReturn =  (String) Utils.executeSimpleMethod(ctrlObj, methodName);
            
                out.print("After executing the "+ methodName +" method in the "+ className +".class, this method returned the value: ");
                out.println(methodReturn);
            } 
            if (controllerMethod.getReturnType() == ModelView.class) {
                ModelView modelView = (ModelView) Utils.executeSimpleMethod(ctrlObj, methodName);
                model.dispatchModelView(modelView, request, response);
            }
        } else {
            throw new ReturnException(methodName, className);
        }
    }

    public String[] getParameterName(Method method){
        Paranamer paranamer = new AdaptiveParanamer();
        String [] parameterName = paranamer.lookupParameterNames(method);
        return  parameterName;
    }

    public Object prepareObject (String name, Object object, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Field[] attributs = object.getClass().getDeclaredFields();
        ValidationManager checker = new ValidationManager();
        boolean isErrorPresent = false;

        for (Field attribut : attributs){
            
            String attribut_name = attribut.getName();
            String method_name = "set" + Utils.toUpperCase(attribut_name);
            Method method = object.getClass().getDeclaredMethod(method_name, attribut.getType());
            String input_name = name + ":" + attribut_name;
            String value = request.getParameter(input_name); 

            if(value != null){
                String error = checker.isFieldValid(attribut, value);
                if (error == null) {
                    method.invoke(object, Utils.castValue(value, attribut.getType()));
                    String key = "value_" + attribut_name;
                    request.setAttribute(key, value);
                } else {
                    String key = "error_" + attribut_name;
                    request.setAttribute(key, error);
                    isErrorPresent = true;
                }
            }
        }

        if (isErrorPresent) {
            String page = checker.getPreviousPage(request);
            request.getRequestDispatcher("/"+page+"?previous_page="+page).forward(request, response);
        }
        return object;
    }

    public void returnJson(Object obj, Method method, HttpServletResponse response) throws Exception {
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (method.getReturnType() == ModelView.class) {
            ModelView modelView = (ModelView) obj;
            for(Map.Entry<String,Object> data : modelView.getData().entrySet()){
                String jsonValue = gson.toJson(data.getValue());
                out.println(jsonValue);
            }
        } else {
            String jsonValue = gson.toJson(obj);
            out.println(jsonValue);
        }
    }

    
    public List<Object> preparedParameter(Object obj, Method method, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Object> result = new ArrayList<>();
        Parameter [] parameters = method.getParameters();
        String [] parameterName = getParameterName(method);

        for (int i = 0 ; i < parameters.length; i++){
            Annotation argumentAnnotation = parameters[i].getAnnotation(RequestParam.class);
            String argumentName = parameterName[i];
            if (argumentAnnotation != null){
                argumentName = ((RequestParam) argumentAnnotation).value();
            } 
            
            Class<?> clazz = parameters[i].getType();

            if(clazz == Session.class){
                Session session = new Session(request.getSession());
                result.add(session);
            }
            if(clazz == Part.class){
                result.add(request.getPart(argumentName));
            }
            if (Utils.isObject(clazz) && clazz != Session.class && clazz != Part.class){
                Annotation class_annotation = clazz.getAnnotation(Authentication.class);
                if (new AuthenticationManager().isHabilitate(class_annotation, request)) {
                    Object o = clazz.newInstance();
                    result.add(this.prepareObject(argumentName, o, request, response));
                } else{
                    throw new AuthenticationException("the " + clazz.getName() + " class");
                }
            }
            if (!Utils.isObject(clazz)) {
                
                if(request.getParameter(argumentName)!=null){
                    result.add(Utils.castValue(request.getParameter(argumentName),parameters[i].getType()));
                }
            }
        }

        return result;
    }

    public void executeMethod (String packageCtrl, Mapping map, VerbAction verbAction, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PrintWriter out = response.getWriter();
        ModelView model = new ModelView();
        
        Class<?> clazz = Class.forName(packageCtrl+"."+map.getClassName());
        Annotation class_anotation = clazz.getAnnotation(Authentication.class);
        if (new AuthenticationManager().isHabilitate(class_anotation, request)) {

            Method method = Utils.getMethodAnnoted(clazz, verbAction.getMethod());
            Annotation method_annotation = method.getAnnotation(Authentication.class);
            if (new AuthenticationManager().isHabilitate(method_annotation, request)) {
                Object object = clazz.newInstance();
                new Session().addSession(object, request);
                
                List<Object> methodParameters = new ArrayList<>();
                
                if (method.getParameters().length > 0) {
                    methodParameters = preparedParameter(object, method,request, response);

                    if (methodParameters.size() != method.getParameters().length) {
                        throw new Exception("Parameters number is insufficient!");
                    }
                }

                if (method.isAnnotationPresent(RestAPI.class)) {
                    Object obj = method.invoke(object, methodParameters.toArray(new Object[]{}));
                    returnJson(obj, method, response);

                } else {
                    if (method.getReturnType() == String.class){
                        out.println("Method return : "+ method.invoke(object, methodParameters.toArray(new Object[]{})).toString());
                    }
                    else if (method.getReturnType() == ModelView.class){
                        model.dispatchModelView((ModelView) method.invoke(object, methodParameters.toArray(new Object[]{})), request, response);
                    }
                    else {
                        throw new Exception("The return type of the method " + method.getName() + " in " + clazz.getName() + " class is invalid!");
                    }
                }

            } else {
                throw new AuthenticationException("the "+ method.getName() +" method of the controller " + clazz.getName()+" class");
            }

        } else {
            throw new AuthenticationException("the controller " + clazz.getName() + " class");
        }        
    }
}
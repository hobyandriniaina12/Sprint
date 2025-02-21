package mg.itu.prom16.Util;

import java.lang.reflect.Method;
import java.util.HashMap;

public class Mapping 
{
    String className;
    String methodName;
    HashMap<String, Method> methods = new HashMap<>();

    public boolean havePost(){
        if (this.getMethods().containsKey("POST")) {
            return true;
        }
        return false;
    }

    public boolean haveGet(){
        if (this.getMethods().containsKey("GET")) {
            return true;
        }
        return false;
    }

    public Mapping(String className, HashMap<String, Method> methods) {
        this.className = className;
        this.methods = methods;
    }

    public HashMap<String, Method> getMethods() {
        return methods;
    }

    public Method getMethod(String key){
        return this.methods.get(key);
    }

    public void setMethods(HashMap<String, Method> methods) {
        this.methods = methods;
    }

    public void addMethod(String verbe, Method method){
        this.methods.put(verbe, method);
    }

    public Mapping(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }



    public Mapping() {
    }

    // public Mapping(String className, Method methodName) {
    //     this.className = className;
    //     this.method = methodName;
    // }

    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
}

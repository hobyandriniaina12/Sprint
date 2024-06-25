package mg.itu.prom16.Util;

import java.lang.reflect.Method;

public class Mapping 
{
    String className;
    String methodName;
    Method method;

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

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Mapping() {
    }

    public Mapping(String className, Method methodName) {
        this.className = className;
        this.method = methodName;
    }

    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
}

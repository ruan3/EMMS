package com.esquel.epass.lib.flipview;

/**
 * 
 * @author hung
 * 
 */
public class Log {

    public static void log(Object o) {
        String message = "" + o;
        final int currentThread = 3;
        String fullClassName = Thread.currentThread().getStackTrace()[currentThread].getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName = Thread.currentThread().getStackTrace()[currentThread].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[currentThread].getLineNumber();

        System.out.println("tmh " + className + "." + methodName + "():" + lineNumber + " | " + message);

    }
}

package com.parag.lily;


import java.util.logging.Logger;

public final class Utility {

    public static Logger getLogger() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String name =  stackTrace[stackTrace.length - 1].getClassName();
        return Logger.getLogger(name);
    }
}

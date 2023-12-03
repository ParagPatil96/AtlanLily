package com.parag.lily;


import com.google.common.base.Strings;

import java.util.logging.Logger;

public final class Utility {

    public static Logger getLogger() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String name =  stackTrace[stackTrace.length - 1].getClassName();
        return Logger.getLogger(name);
    }

    public static <T> T getSystemProperty(String key, Class<T> type){
        String value = System.getProperty(key);
        if(Strings.isNullOrEmpty(value)){
            value = System.getenv(key);
            if(Strings.isNullOrEmpty(value)){
                throw new RuntimeException("Unable to find system value for key " + key);
            }
        }
        return (T) value;
    }
}

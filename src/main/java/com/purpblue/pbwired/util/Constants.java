package com.purpblue.pbwired.util;

/**
 * @author Purpblue
 */
public class Constants {

    /**
     * Already has @Autowired-annotated constructor
     */
    public static final int AUTOWIRED_CTOR = 1;

    // Some useful Strings
    public static final String STRING_VALUE = "value";
    public static final String STRING_THIS = "this";
    public static final String STRING_AUTOWIRED = "Autowired";
    public static final String STRING_CTOR = "<init>";
    public static final String MAP_CAPITALIZED = "Map";
    public static final String DOT_CLASS_STRING = ".class";


    // Some annotation paths
    public static final String AUTOWIRED_PATH = "org.springframework.beans.factory.annotation.Autowired";
    public static final String QUALIFIER_PATH = "org.springframework.beans.factory.annotation.Qualifier";
    public static final String VALUE_PATH = "org.springframework.beans.factory.annotation.Value";

    // Resolve Method qualified path
    public static final String VALUE_UTIL_BUILD_METHOD = "com.purpblue.pbwired.util.ValueResolver.getProperty";

}

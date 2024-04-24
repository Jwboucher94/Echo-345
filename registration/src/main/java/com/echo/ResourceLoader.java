package com.echo;

import java.io.IOException;
// import java.net.URL;
// import java.util.Enumeration;
// import java.util.Collections;

public class ResourceLoader {
    // Get the absolute path of the file
    public String getResourcePath(String filename) throws IOException{
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        /* Enumeration<URL> resources = classLoader.getResources("");  
        for (URL resource : Collections.list(resources)) {
            System.out.println(resource.getFile());
        } */
        return classLoader.getResource(filename).getFile();
    }
}
